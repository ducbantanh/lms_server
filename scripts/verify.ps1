<#
.SYNOPSIS
  Đối chiếu storage_key trong DB (course_files, submission_files) với file trong thư mục lưu trữ.

.DESCRIPTION
  - Thiếu file (DB có, đĩa không có): lỗi, exit code 1.
  - File mồ côi (đĩa có, DB không có): chỉ cảnh báo. Có thể là file rác do xóa lỗi,
    hoặc file đang upload dở lúc kiểm tra; không tự xóa.

.EXAMPLE
  .\scripts\verify.ps1
#>
[CmdletBinding()]
param(
    [string]$Database,
    [string]$StorageDir,
    [string]$EnvFile,
    [string]$DockerContainer,
    [string]$MysqlBin
)

. (Join-Path $PSScriptRoot 'lms-common.ps1')

$ctx = New-LmsContext -EnvFile $EnvFile -DockerContainer $DockerContainer -MysqlBin $MysqlBin
$db = if ($Database) { $Database } else { $ctx.Database }
Assert-SqlName $db
$dir = if ($StorageDir) { $ExecutionContext.SessionState.Path.GetUnresolvedProviderPathFromPSPath($StorageDir) } else { $ctx.StorageDir }

$r = Get-StorageReport $ctx $db $dir
Write-Host "Database '$db': $($r.Referenced) file được tham chiếu. Thư mục ${dir}: $($r.OnDisk) file."

if ($r.Orphans.Count) {
    Write-Warning "$($r.Orphans.Count) file mồ côi (không có trong DB):"
    $r.Orphans | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    if ($r.Orphans.Count -gt 20) { Write-Host "  ... và $($r.Orphans.Count - 20) file khác" }
}
if ($r.Missing.Count) {
    Write-Host "LỖI: thiếu $($r.Missing.Count) file mà DB tham chiếu:" -ForegroundColor Red
    $r.Missing | ForEach-Object { Write-Host "  $_" }
    exit 1
}
Write-Host 'OK: mọi file DB tham chiếu đều có trên đĩa.' -ForegroundColor Green
