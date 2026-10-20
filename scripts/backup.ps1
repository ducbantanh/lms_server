<#
.SYNOPSIS
  Sao lưu LMS: database MySQL + thư mục file (tài liệu, đề, bài nộp) vào một file zip.

.DESCRIPTION
  Dump DB trước (mysqldump --single-transaction, không cần dừng server), copy file sau.
  File lưu trữ không bao giờ bị ghi đè (FileStorage dùng CREATE_NEW) và chỉ bị xóa sau khi DB commit,
  nên thư mục file copy sau luôn chứa đủ file mà bản dump tham chiếu (có thể dư vài file mới upload).

  Zip gồm: db.sql, files/<storage_key>, manifest.json (thời điểm, version schema, sha256 từng file).
  Chỉ các bản backup thường (không có -Label) bị xoay vòng theo -Keep; bản có nhãn được giữ lại.

  Backup chứa hash mật khẩu, thông tin sinh viên và bài nộp: lưu ở thư mục giới hạn quyền,
  ngoài thư mục web, tốt nhất là ổ/máy khác. File .env không nằm trong backup.

.EXAMPLE
  .\scripts\backup.ps1
.EXAMPLE
  .\scripts\backup.ps1 -OutDir D:\lms-backups -Keep 30
.EXAMPLE
  .\scripts\backup.ps1 -DockerContainer lms-mysql
#>
[CmdletBinding()]
param(
    # Thư mục chứa file backup. Mặc định lms_server\backups
    [string]$OutDir,
    # Số bản backup thường giữ lại
    [ValidateRange(1, 1000)][int]$Keep = 14,
    # Nhãn gắn vào tên file, ví dụ pre-restore
    [string]$Label,
    [string]$EnvFile,
    [string]$DockerContainer,
    [string]$MysqlBin
)

. (Join-Path $PSScriptRoot 'lms-common.ps1')

if ($Label -and $Label -notmatch '^[a-z0-9-]{1,32}$') { throw 'Label chỉ gồm chữ thường, số và dấu -' }

$ctx = New-LmsContext -EnvFile $EnvFile -DockerContainer $DockerContainer -MysqlBin $MysqlBin
if (-not $OutDir) { $OutDir = Join-Path $script:ServerRoot 'backups' }
New-Item -ItemType Directory -Force -Path $OutDir | Out-Null
$OutDir = (Resolve-Path -LiteralPath $OutDir).Path

$stamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$name = "lms-backup-$stamp" + $(if ($Label) { "-$Label" } else { '' })
$work = Join-Path $OutDir ".tmp-$name"
$zip = Join-Path $OutDir "$name.zip"
$partial = "$zip.partial"

try {
    $filesDir = Join-Path $work 'files'
    New-Item -ItemType Directory -Path $filesDir | Out-Null

    Write-Step "Dump database $($ctx.Database)"
    $schema = Get-SchemaVersion $ctx $ctx.Database
    $dump = Join-Path $work 'db.sql'
    Invoke-LmsMySql -Ctx $ctx -Tool mysqldump -StdoutFile $dump -Arguments @(
        '--single-transaction', '--quick', '--hex-blob', '--no-tablespaces', '--set-gtid-purged=OFF', $ctx.Database)

    # Sau bản dump: xem chú thích đầu file
    Write-Step "Copy file từ $($ctx.StorageDir)"
    $files = New-Object 'System.Collections.Generic.List[object]'
    if (Test-Path -LiteralPath $ctx.StorageDir) {
        foreach ($f in Get-ChildItem -LiteralPath $ctx.StorageDir -File) {
            if ($f.Attributes -band [IO.FileAttributes]::ReparsePoint) { Write-Warning "Bỏ qua symlink: $($f.Name)"; continue }
            if ($f.Name -cnotmatch $script:StorageKeyPattern) { Write-Warning "Bỏ qua file lạ: $($f.Name)"; continue }
            $copy = Join-Path $filesDir $f.Name
            try {
                Copy-Item -LiteralPath $f.FullName -Destination $copy
            } catch [System.Management.Automation.ItemNotFoundException] {
                Write-Warning "File bị xóa trong lúc backup: $($f.Name)"
                continue
            }
            $files.Add([ordered]@{ name = $f.Name; bytes = $f.Length; sha256 = Get-Sha256 $copy })
        }
    } else {
        Write-Warning "Chưa có thư mục file $($ctx.StorageDir)"
    }

    $manifest = [ordered]@{
        format        = 1
        createdAt     = (Get-Date).ToUniversalTime().ToString('o')
        label         = $Label
        database      = $ctx.Database
        schemaVersion = $schema
        dump          = [ordered]@{ name = 'db.sql'; bytes = (Get-Item -LiteralPath $dump).Length; sha256 = Get-Sha256 $dump }
        files         = $files
    }
    Write-Utf8File (Join-Path $work 'manifest.json') ($manifest | ConvertTo-Json -Depth 4)

    Write-Step 'Nén'
    # Tự thêm entry thay vì CreateFromDirectory: .NET Framework ghi tên entry bằng '\', giải nén trên Linux sẽ sai
    Add-Type -AssemblyName System.IO.Compression, System.IO.Compression.FileSystem
    $archive = [IO.Compression.ZipFile]::Open($partial, [IO.Compression.ZipArchiveMode]::Create)
    try {
        foreach ($f in Get-ChildItem -LiteralPath $work -File -Recurse) {
            $entry = $f.FullName.Substring($work.Length + 1).Replace('\', '/')
            [void][IO.Compression.ZipFileExtensions]::CreateEntryFromFile($archive, $f.FullName, $entry, [IO.Compression.CompressionLevel]::Optimal)
        }
    } finally {
        $archive.Dispose()
    }
    Move-Item -LiteralPath $partial -Destination $zip
} finally {
    Remove-Item -LiteralPath $work -Recurse -Force -ErrorAction SilentlyContinue
    Remove-Item -LiteralPath $partial -Force -ErrorAction SilentlyContinue
}

$old = @(Get-ChildItem -LiteralPath $OutDir -Filter 'lms-backup-*.zip' -File |
    Where-Object { $_.Name -match '^lms-backup-\d{8}-\d{6}\.zip$' } |
    Sort-Object Name -Descending | Select-Object -Skip $Keep)
foreach ($o in $old) {
    Remove-Item -LiteralPath $o.FullName -Force
    Write-Host "Đã xóa bản cũ: $($o.Name)"
}

Write-LmsAudit $ctx $ctx.Database 'system.backup' "$name.zip, $($files.Count) files"

$sizeMb = [Math]::Round((Get-Item -LiteralPath $zip).Length / 1MB, 2)
Write-Host "Xong: $zip ($sizeMb MB, schema V$schema, $($files.Count) file)" -ForegroundColor Green
$zip
