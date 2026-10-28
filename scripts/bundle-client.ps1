# Chép bản build tĩnh từ lms-client/out vào lms-server/src/main/resources/static
$clientOut = Join-Path $PSScriptRoot "../../lms-client/out"
$serverStatic = Join-Path $PSScriptRoot "../src/main/resources/static"

if (-not (Test-Path $clientOut)) {
    Write-Error "Chưa tìm thấy $clientOut. Hãy vào lms-client chạy 'npm run build' trước."
    exit 1
}

Write-Host "Đang sao chép giao diện Next.js vào Spring Boot static resources..."
Copy-Item -Path "$clientOut/*" -Destination $serverStatic -Recurse -Force
Write-Host "Hoàn tất! Spring Boot đã sẵn sàng phục vụ frontend." -ForegroundColor Green
