# Hàm dùng chung cho backup.ps1, restore.ps1, verify.ps1, test-restore.ps1 (dot-source, không chạy trực tiếp).
# Cấu hình đọc giống server: biến môi trường > file .env > mặc định trong application.yml.

$ErrorActionPreference = 'Stop'

$script:ServerRoot = Split-Path -Parent $PSScriptRoot
$script:StorageKeyPattern = '^[A-Za-z0-9_-]{43}$'   # giống FileStorage.KEY
$script:SqlNamePattern = '^[A-Za-z0-9_]{1,64}$'
$script:Bt = [char]96                                # dấu ` quanh tên bảng/database trong SQL

function Write-Step([string]$Message) {
    Write-Host "==> $Message" -ForegroundColor Cyan
}

function Write-Utf8File([string]$Path, [string]$Content) {
    [IO.File]::WriteAllText($Path, $Content, (New-Object Text.UTF8Encoding $false))
}

function Assert-SqlName([string]$Name) {
    if ($Name -notmatch $script:SqlNamePattern) { throw "Tên không hợp lệ: '$Name' (chỉ cho phép chữ, số, _)" }
}

function New-OrdinalSet {
    New-Object 'System.Collections.Generic.HashSet[string]' ([StringComparer]::Ordinal)
}

function Read-DotEnv([string]$Path) {
    $values = @{}
    if (-not (Test-Path -LiteralPath $Path)) { return $values }
    foreach ($line in [IO.File]::ReadAllLines($Path)) {
        $t = $line.Trim()
        if ($t -eq '' -or $t.StartsWith('#')) { continue }
        $i = $t.IndexOf('=')
        if ($i -lt 1) { continue }
        $values[$t.Substring(0, $i).Trim()] = $t.Substring($i + 1).Trim()
    }
    $values
}

function Get-Setting($DotEnv, [string]$Name, [string]$Default) {
    $fromEnv = [Environment]::GetEnvironmentVariable($Name)
    if ($fromEnv) { return $fromEnv }
    if ($DotEnv.ContainsKey($Name) -and $DotEnv[$Name] -ne '') { return $DotEnv[$Name] }
    $Default
}

function Find-MySqlBin([string]$Hint) {
    $candidates = @()
    if ($Hint) { $candidates += $Hint }
    $cmd = Get-Command mysqldump.exe -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($cmd) { $candidates += (Split-Path -Parent $cmd.Source) }
    $candidates += @(Get-ChildItem (Join-Path $env:ProgramFiles 'MySQL') -Directory -Filter 'MySQL Server*' -ErrorAction SilentlyContinue |
        Sort-Object Name -Descending | ForEach-Object { Join-Path $_.FullName 'bin' })
    foreach ($dir in $candidates) {
        if (Test-Path -LiteralPath (Join-Path $dir 'mysqldump.exe')) { return $dir }
    }
    throw 'Không tìm thấy mysqldump.exe. Thêm thư mục bin của MySQL vào PATH, truyền -MysqlBin, hoặc dùng -DockerContainer.'
}

# Thông tin kết nối DB + thư mục file. DockerContainer: chạy mysql/mysqldump bên trong container (docker-compose.yml).
function New-LmsContext([string]$EnvFile, [string]$DockerContainer, [string]$MysqlBin) {
    if (-not $EnvFile) { $EnvFile = Join-Path $script:ServerRoot '.env' }
    $dotEnv = Read-DotEnv $EnvFile
    $url = Get-Setting $dotEnv 'APP_DB_URL' 'jdbc:mysql://localhost:3306/lms'
    if ($url -notmatch '^jdbc:mysql://([^:/?]+)(?::(\d+))?/([^?;]+)') { throw "Không đọc được APP_DB_URL: $url" }
    $dbHost = $Matches[1]
    $dbPort = if ($Matches[2]) { [int]$Matches[2] } else { 3306 }
    $db = $Matches[3]
    Assert-SqlName $db

    # Server chạy từ thư mục lms_server nên đường dẫn tương đối tính từ đó
    $storage = Get-Setting $dotEnv 'APP_STORAGE_DIR' 'data/files'
    if (-not [IO.Path]::IsPathRooted($storage)) { $storage = Join-Path $script:ServerRoot $storage }

    [pscustomobject]@{
        Host       = $dbHost
        Port       = $dbPort
        Database   = $db
        User       = Get-Setting $dotEnv 'APP_DB_USER' 'lms'
        Password   = Get-Setting $dotEnv 'APP_DB_PASSWORD' ''
        StorageDir = [IO.Path]::GetFullPath($storage).TrimEnd('\', '/')
        AppPort    = [int](Get-Setting $dotEnv 'APP_PORT' '8080')
        Docker     = $DockerContainer
        Bin        = if ($DockerContainer) { $null } else { Find-MySqlBin $MysqlBin }
    }
}

# Ghép tham số theo quy tắc dòng lệnh Windows (Start-Process chỉ nhận một chuỗi).
function Join-NativeArgs([string[]]$Arguments) {
    ($Arguments | ForEach-Object {
        if ($_ -eq '') { '""' }
        elseif ($_ -match '[\s"]') { '"' + ($_ -replace '(\\*)"', '$1$1\"' -replace '(\\+)$', '$1$1') + '"' }
        else { $_ }
    }) -join ' '
}

function Format-OptionValue([string]$Value) {
    '"' + ($Value -replace '\\', '\\' -replace '"', '\"') + '"'
}

# Chạy mysql/mysqldump. stdin/stdout đi qua file để giữ nguyên byte (PowerShell 5.1 làm hỏng encoding khi pipe).
# Mật khẩu không nằm trên dòng lệnh: native dùng option file tạm, Docker dùng biến MYSQL_PWD.
function Invoke-LmsMySql {
    param(
        $Ctx,
        [ValidateSet('mysql', 'mysqldump')][string]$Tool,
        [string[]]$Arguments,
        [string]$StdinFile,
        [string]$StdoutFile
    )
    $tmpOut = $null
    $optFile = $null
    $errFile = [IO.Path]::GetTempFileName()
    if (-not $StdoutFile) { $tmpOut = [IO.Path]::GetTempFileName(); $StdoutFile = $tmpOut }
    try {
        if ($Ctx.Docker) {
            $exe = 'docker'
            $all = @('exec')
            if ($StdinFile) { $all += '-i' }
            $all += @('-e', 'MYSQL_PWD', $Ctx.Docker, $Tool, "--user=$($Ctx.User)", '--default-character-set=utf8mb4') + $Arguments
            $env:MYSQL_PWD = $Ctx.Password
        } else {
            $exe = Join-Path $Ctx.Bin "$Tool.exe"
            $optFile = [IO.Path]::GetTempFileName()
            Write-Utf8File $optFile ("[client]`r`nuser=$(Format-OptionValue $Ctx.User)`r`npassword=$(Format-OptionValue $Ctx.Password)`r`n" +
                "host=$(Format-OptionValue $Ctx.Host)`r`nport=$($Ctx.Port)`r`ndefault-character-set=utf8mb4`r`n")
            # --defaults-extra-file bắt buộc đứng đầu
            $all = @("--defaults-extra-file=$optFile") + $Arguments
        }
        $p = @{
            FilePath               = $exe
            ArgumentList           = (Join-NativeArgs $all)
            NoNewWindow            = $true
            Wait                   = $true
            PassThru               = $true
            RedirectStandardOutput = $StdoutFile
            RedirectStandardError  = $errFile
        }
        if ($StdinFile) { $p.RedirectStandardInput = $StdinFile }
        $proc = Start-Process @p
        $stderr = [IO.File]::ReadAllText($errFile).Trim()
        if ($proc.ExitCode -ne 0) { throw "$Tool lỗi (mã $($proc.ExitCode)): $stderr" }
        if ($stderr) { Write-Warning "${Tool}: $stderr" }
        if ($tmpOut) { return [IO.File]::ReadAllText($tmpOut, [Text.Encoding]::UTF8) }
    } finally {
        if ($Ctx.Docker) { Remove-Item Env:MYSQL_PWD -ErrorAction SilentlyContinue }
        foreach ($f in @($tmpOut, $optFile, $errFile)) {
            if ($f) { Remove-Item -LiteralPath $f -Force -ErrorAction SilentlyContinue }
        }
    }
}

# Chạy SQL, trả về từng dòng kết quả (cột cách nhau bằng tab). SQL chỉ dùng nháy đơn.
function Invoke-LmsQuery($Ctx, [string]$Database, [string]$Sql) {
    $cliArgs = @('--batch', '--skip-column-names')
    if ($Database) { $cliArgs += "--database=$Database" }
    $cliArgs += "--execute=$Sql"
    $out = Invoke-LmsMySql -Ctx $Ctx -Tool mysql -Arguments $cliArgs
    @($out -split "`r?`n" | Where-Object { $_ -ne '' })
}

function Test-DatabaseExists($Ctx, [string]$Database) {
    Assert-SqlName $Database
    @(Invoke-LmsQuery $Ctx '' "SELECT SCHEMA_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = '$Database'").Count -gt 0
}

# Version Flyway mới nhất đã chạy thành công; $null nếu chưa có bảng flyway_schema_history.
function Get-SchemaVersion($Ctx, [string]$Database) {
    try {
        $rows = @(Invoke-LmsQuery $Ctx $Database ('SELECT version FROM flyway_schema_history ' +
                'WHERE success = 1 AND version IS NOT NULL ORDER BY installed_rank DESC LIMIT 1'))
        if ($rows.Count) { return $rows[0] }
    } catch {
        Write-Warning "Không đọc được version schema: $($_.Exception.Message)"
    }
    $null
}

# Version migration lớn nhất trong source; $null nếu không có thư mục migration (ví dụ chỉ có file jar).
function Get-CodeSchemaVersion {
    $dir = Join-Path $script:ServerRoot 'src\main\resources\db\migration'
    if (-not (Test-Path -LiteralPath $dir)) { return $null }
    $versions = @(Get-ChildItem -LiteralPath $dir -Filter 'V*__*.sql' | ForEach-Object {
        if ($_.Name -match '^V([\d_.]+)__') { $Matches[1] -replace '_', '.' }
    })
    if (-not $versions.Count) { return $null }
    $max = $versions[0]
    foreach ($v in $versions) { if ((Compare-SchemaVersion $v $max) -gt 0) { $max = $v } }
    $max
}

function Compare-SchemaVersion([string]$A, [string]$B) {
    $pa = @($A.Split('.') | ForEach-Object { [long]$_ })
    $pb = @($B.Split('.') | ForEach-Object { [long]$_ })
    for ($i = 0; $i -lt [Math]::Max($pa.Count, $pb.Count); $i++) {
        $x = if ($i -lt $pa.Count) { $pa[$i] } else { 0 }
        $y = if ($i -lt $pb.Count) { $pb[$i] } else { 0 }
        if ($x -ne $y) { return [Math]::Sign($x - $y) }
    }
    0
}

function Get-Sha256([string]$Path) {
    (Get-FileHash -LiteralPath $Path -Algorithm SHA256).Hash.ToLowerInvariant()
}

# Đối chiếu storage_key trong DB với file trên đĩa.
# Missing: DB có mà đĩa không có (lỗi). Orphans: đĩa có mà DB không có (file rác hoặc upload đang dở).
function Get-StorageReport($Ctx, [string]$Database, [string]$StorageDir) {
    $keys = @(Invoke-LmsQuery $Ctx $Database 'SELECT storage_key FROM course_files UNION SELECT storage_key FROM submission_files')
    $onDisk = New-OrdinalSet
    if (Test-Path -LiteralPath $StorageDir) {
        foreach ($f in Get-ChildItem -LiteralPath $StorageDir -File) { [void]$onDisk.Add($f.Name) }
    }
    $referenced = New-OrdinalSet
    foreach ($k in $keys) { [void]$referenced.Add($k) }
    [pscustomobject]@{
        Referenced = $referenced.Count
        OnDisk     = $onDisk.Count
        Missing    = @($keys | Where-Object { -not $onDisk.Contains($_) })
        Orphans    = @($onDisk | Where-Object { -not $referenced.Contains($_) })
    }
}

# Ghi audit_logs giống AuditService (actor NULL = hệ thống). Lỗi audit không làm hỏng thao tác chính.
# Detail chỉ dùng ASCII: tham số dòng lệnh của mysql.exe đi qua code page ANSI.
function Write-LmsAudit($Ctx, [string]$Database, [string]$Action, [string]$Detail) {
    $d = $Detail -replace '\\', '\\' -replace "'", "''"
    try {
        Invoke-LmsQuery $Ctx $Database ("INSERT INTO audit_logs (actor_user_id, action, target, detail, ip, created_at) " +
            "VALUES (NULL, '$Action', NULL, '$d', NULL, UTC_TIMESTAMP(3))") | Out-Null
    } catch {
        Write-Warning "Không ghi được audit log ${Action}: $($_.Exception.Message)"
    }
}
