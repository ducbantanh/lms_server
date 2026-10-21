<#
.SYNOPSIS
  Khôi phục LMS từ file zip do backup.ps1 tạo. XÓA toàn bộ dữ liệu hiện tại của database và thư mục file đích.

.DESCRIPTION
  1. Giải nén, kiểm tra sha256 của bản dump và từng file theo manifest.
  2. Từ chối nếu schema của backup mới hơn migration trong source (cập nhật code trước).
  3. Khôi phục vào hệ thống đang dùng: yêu cầu server đã dừng, tự backup trạng thái hiện tại (nhãn pre-restore).
  4. Xóa mọi bảng của database đích, import bản dump.
  5. Thay thư mục file bằng file trong backup.
  6. Xóa bảng sessions: mọi người phải đăng nhập lại.
  7. Đối chiếu DB với file.
  Sau đó khởi động server; nếu code mới hơn backup, Flyway tự migrate tiếp.

  Khôi phục thử vào database khác thì dùng test-restore.ps1.

.EXAMPLE
  .\scripts\restore.ps1 -BackupFile D:\lms-backups\lms-backup-20261020-230000.zip
#>
[CmdletBinding()]
param(
    [Parameter(Mandatory)][string]$BackupFile,
    # Database đích. Mặc định là database trong .env (hệ thống đang dùng)
    [string]$Database,
    # Thư mục file đích. Bắt buộc khi -Database khác database trong .env
    [string]$StorageDir,
    # Nơi lưu bản backup an toàn trước khi khôi phục. Mặc định lms_server\backups
    [string]$BackupDir,
    [switch]$SkipSafetyBackup,
    # Không hỏi xác nhận
    [switch]$Force,
    [string]$EnvFile,
    [string]$DockerContainer,
    [string]$MysqlBin
)

. (Join-Path $PSScriptRoot 'lms-common.ps1')

$ctx = New-LmsContext -EnvFile $EnvFile -DockerContainer $DockerContainer -MysqlBin $MysqlBin
$BackupFile = (Resolve-Path -LiteralPath $BackupFile).Path

$target = if ($Database) { $Database } else { $ctx.Database }
Assert-SqlName $target
$isLive = $target -eq $ctx.Database
if ($StorageDir) {
    $targetStorage = $ExecutionContext.SessionState.Path.GetUnresolvedProviderPathFromPSPath($StorageDir).TrimEnd('\', '/')
} elseif ($isLive) {
    $targetStorage = $ctx.StorageDir
} else {
    throw 'Khôi phục vào database khác thì phải truyền -StorageDir, tránh ghi đè file của hệ thống đang chạy.'
}
if (-not $isLive) {
    if ($targetStorage -eq $ctx.StorageDir) { throw '-StorageDir trùng thư mục file của hệ thống đang chạy.' }
    if ((Test-Path -LiteralPath $targetStorage) -and @(Get-ChildItem -LiteralPath $targetStorage -Force).Count) {
        throw "Thư mục $targetStorage không trống."
    }
}

$stamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$extract = Join-Path ([IO.Path]::GetTempPath()) ('lms-restore-' + [guid]::NewGuid().ToString('N'))

try {
    Write-Step "Giải nén và kiểm tra $(Split-Path -Leaf $BackupFile)"
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    [IO.Compression.ZipFile]::ExtractToDirectory($BackupFile, $extract)
    $manifestPath = Join-Path $extract 'manifest.json'
    if (-not (Test-Path -LiteralPath $manifestPath)) { throw 'Không phải file backup LMS (thiếu manifest.json).' }
    $m = [IO.File]::ReadAllText($manifestPath) | ConvertFrom-Json
    if ($m.format -ne 1) { throw "Không hỗ trợ định dạng backup $($m.format)." }

    $dump = Join-Path $extract 'db.sql'
    if (-not (Test-Path -LiteralPath $dump) -or (Get-Sha256 $dump) -ne $m.dump.sha256) { throw 'db.sql sai checksum: file backup bị hỏng.' }

    $filesDir = Join-Path $extract 'files'
    New-Item -ItemType Directory -Force -Path $filesDir | Out-Null
    $listed = New-OrdinalSet
    foreach ($f in @($m.files)) {
        if ($f.name -cnotmatch $script:StorageKeyPattern) { throw "Tên file không hợp lệ trong manifest: $($f.name)" }
        $p = Join-Path $filesDir $f.name
        if (-not (Test-Path -LiteralPath $p) -or (Get-Sha256 $p) -ne $f.sha256) { throw "File $($f.name) thiếu hoặc sai checksum." }
        [void]$listed.Add($f.name)
    }
    foreach ($f in Get-ChildItem -LiteralPath $filesDir -Force) {
        if (-not $listed.Contains($f.Name)) { throw "File $($f.Name) không có trong manifest." }
    }

    $codeVersion = Get-CodeSchemaVersion
    if ($m.schemaVersion -and $codeVersion -and (Compare-SchemaVersion $m.schemaVersion $codeVersion) -gt 0) {
        throw "Backup ở schema V$($m.schemaVersion), mới hơn code hiện tại (V$codeVersion). Cập nhật code trước rồi mới khôi phục."
    }
    if (-not $codeVersion) { Write-Warning 'Không tìm thấy thư mục migration, bỏ qua kiểm tra version schema.' }

    if ($isLive -and (Get-Command Get-NetTCPConnection -ErrorAction SilentlyContinue)) {
        if (Get-NetTCPConnection -State Listen -LocalPort $ctx.AppPort -ErrorAction SilentlyContinue) {
            throw "Có tiến trình đang nghe cổng $($ctx.AppPort) (server LMS?). Dừng server trước khi khôi phục."
        }
    }

    $createdAt = ([datetime]$m.createdAt).ToLocalTime().ToString('yyyy-MM-dd HH:mm:ss')
    Write-Host ''
    Write-Host "  Backup : $createdAt, schema V$($m.schemaVersion), $($listed.Count) file"
    Write-Host "  Đích   : database '$target' trên $($ctx.Host):$($ctx.Port)$(if ($isLive) { ' (HỆ THỐNG ĐANG DÙNG)' })"
    Write-Host "           thư mục $targetStorage"
    Write-Host '  Toàn bộ dữ liệu hiện tại ở đích sẽ bị thay thế.' -ForegroundColor Yellow
    Write-Host ''
    if (-not $Force) {
        $answer = Read-Host "Gõ tên database ($target) để xác nhận"
        if ($answer -cne $target) { Write-Host 'Đã hủy.'; exit 1 }
    }

    $dbExists = Test-DatabaseExists $ctx $target
    $safety = $null
    if ($isLive -and $dbExists -and -not $SkipSafetyBackup) {
        Write-Step 'Backup an toàn trạng thái hiện tại'
        $safety = & (Join-Path $PSScriptRoot 'backup.ps1') -Label 'pre-restore' -OutDir $BackupDir `
            -EnvFile $EnvFile -DockerContainer $DockerContainer -MysqlBin $MysqlBin | Select-Object -Last 1
    }

    Write-Step "Làm trống database $target"
    $bt = $script:Bt
    if ($dbExists) {
        $tables = @(Invoke-LmsQuery $ctx '' ("SELECT TABLE_NAME FROM information_schema.TABLES " +
                "WHERE TABLE_SCHEMA = '$target' AND TABLE_TYPE = 'BASE TABLE'"))
        if ($tables.Count) {
            foreach ($t in $tables) { Assert-SqlName $t }
            $list = ($tables | ForEach-Object { "$bt$_$bt" }) -join ', '
            Invoke-LmsQuery $ctx $target "SET FOREIGN_KEY_CHECKS = 0; DROP TABLE IF EXISTS $list; SET FOREIGN_KEY_CHECKS = 1" | Out-Null
        }
    } else {
        Invoke-LmsQuery $ctx '' "CREATE DATABASE $bt$target$bt CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci" | Out-Null
    }

    Write-Step 'Nhập dữ liệu'
    Invoke-LmsMySql -Ctx $ctx -Tool mysql -Arguments @("--database=$target") -StdinFile $dump | Out-Null
    # Phiên cũ không còn khớp dữ liệu: bắt đăng nhập lại
    Invoke-LmsQuery $ctx $target 'DELETE FROM sessions' | Out-Null

    Write-Step "Thay thư mục file $targetStorage"
    $previous = $null
    if (Test-Path -LiteralPath $targetStorage) {
        $previous = "$targetStorage.before-restore-$stamp"
        Move-Item -LiteralPath $targetStorage -Destination $previous
    }
    New-Item -ItemType Directory -Force -Path $targetStorage | Out-Null
    foreach ($n in $listed) {
        Copy-Item -LiteralPath (Join-Path $filesDir $n) -Destination (Join-Path $targetStorage $n)
    }
    if ($previous) {
        if ($safety -or -not $isLive) {
            Remove-Item -LiteralPath $previous -Recurse -Force
        } else {
            Write-Host "Thư mục file cũ giữ lại tại $previous (không có backup an toàn)."
        }
    }

    Write-Step 'Đối chiếu database với file'
    $report = Get-StorageReport $ctx $target $targetStorage
    $counts = @(Invoke-LmsQuery $ctx $target ('SELECT (SELECT COUNT(*) FROM users), (SELECT COUNT(*) FROM courses), ' +
            '(SELECT COUNT(*) FROM submissions)'))[0] -split "`t"
    Write-Host "  $($counts[0]) tài khoản, $($counts[1]) khóa học, $($counts[2]) bài nộp, $($report.Referenced) file được tham chiếu"
    if ($report.Missing.Count) {
        Write-Warning "$($report.Missing.Count) file được DB tham chiếu nhưng không có trong backup: $($report.Missing -join ', ')"
    }

    Write-LmsAudit $ctx $target 'system.restored' "from $(Split-Path -Leaf $BackupFile)"

    Write-Host ''
    Write-Host "Khôi phục xong database '$target'." -ForegroundColor Green
    if ($safety) { Write-Host "Bản backup trước khi khôi phục: $safety" }
    if ($isLive) { Write-Host 'Khởi động lại server. Mọi người cần đăng nhập lại.' }

    [pscustomobject]@{ Database = $target; StorageDir = $targetStorage; Missing = $report.Missing; SafetyBackup = $safety }
} finally {
    Remove-Item -LiteralPath $extract -Recurse -Force -ErrorAction SilentlyContinue
}
