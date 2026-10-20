<#
.SYNOPSIS
  Kiểm thử một bản backup: khôi phục vào database tạm + thư mục tạm, đối chiếu DB với file, rồi xóa.

.DESCRIPTION
  Không đụng tới database và thư mục file đang dùng. Kết quả được ghi vào audit log của hệ thống đang dùng.
  User MySQL trong .env cần quyền trên database tạm, ví dụ (chạy bằng root):
    CREATE DATABASE lms_restore_test;
    GRANT ALL ON lms_restore_test.* TO 'lms'@'localhost';

.EXAMPLE
  .\scripts\test-restore.ps1
.EXAMPLE
  .\scripts\test-restore.ps1 -BackupFile D:\lms-backups\lms-backup-20261020-230000.zip
#>
[CmdletBinding()]
param(
    # Mặc định: bản mới nhất trong -BackupDir
    [string]$BackupFile,
    [string]$BackupDir,
    [string]$Database = 'lms_restore_test',
    # Giữ lại database/thư mục tạm để xem
    [switch]$Keep,
    [string]$EnvFile,
    [string]$DockerContainer,
    [string]$MysqlBin
)

. (Join-Path $PSScriptRoot 'lms-common.ps1')

$ctx = New-LmsContext -EnvFile $EnvFile -DockerContainer $DockerContainer -MysqlBin $MysqlBin
Assert-SqlName $Database
if ($Database -eq $ctx.Database) { throw 'Database tạm trùng database đang dùng.' }

if (-not $BackupFile) {
    if (-not $BackupDir) { $BackupDir = Join-Path $script:ServerRoot 'backups' }
    $latest = Get-ChildItem -LiteralPath $BackupDir -Filter 'lms-backup-*.zip' -File -ErrorAction SilentlyContinue |
        Sort-Object Name -Descending | Select-Object -First 1
    if (-not $latest) { throw "Không có file backup trong $BackupDir" }
    $BackupFile = $latest.FullName
}
$tmpStorage = Join-Path ([IO.Path]::GetTempPath()) ('lms-restore-test-' + [guid]::NewGuid().ToString('N'))

$ok = $false
try {
    $result = & (Join-Path $PSScriptRoot 'restore.ps1') -BackupFile $BackupFile -Database $Database -StorageDir $tmpStorage `
        -Force -SkipSafetyBackup -EnvFile $EnvFile -DockerContainer $DockerContainer -MysqlBin $MysqlBin | Select-Object -Last 1
    $ok = $result -and $result.Missing.Count -eq 0
} catch {
    Write-Host "Khôi phục thử thất bại: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Message -match 'ERROR 1044') {
        Write-Host "Cấp quyền bằng root: CREATE DATABASE $Database; GRANT ALL ON $Database.* TO '$($ctx.User)'@'localhost';"
    }
} finally {
    if (-not $Keep) {
        try {
            Invoke-LmsQuery $ctx '' "DROP DATABASE IF EXISTS $($script:Bt)$Database$($script:Bt)" | Out-Null
        } catch {
            Write-Warning "Không xóa được database tạm ${Database}: $($_.Exception.Message)"
        }
        Remove-Item -LiteralPath $tmpStorage -Recurse -Force -ErrorAction SilentlyContinue
    } else {
        Write-Host "Giữ lại database '$Database' và thư mục $tmpStorage"
    }
}

$leaf = Split-Path -Leaf $BackupFile
Write-LmsAudit $ctx $ctx.Database 'system.restore_tested' "$leaf $(if ($ok) { 'PASS' } else { 'FAIL' })"
if ($ok) {
    Write-Host "PASS: $leaf khôi phục được và đủ file." -ForegroundColor Green
} else {
    Write-Host "FAIL: $leaf" -ForegroundColor Red
    exit 1
}
