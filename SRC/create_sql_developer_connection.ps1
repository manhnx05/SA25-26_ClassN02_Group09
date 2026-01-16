# Script tự động tạo Oracle SQL Developer Connection
# Đọc cấu hình từ application.properties và tạo connection

param(
    [string]$PropertiesFile = "src/main/resources/application.properties"
)

Write-Host "=== ORACLE SQL DEVELOPER CONNECTION CREATOR ===" -ForegroundColor Green
Write-Host ""

# Đọc application.properties
if (Test-Path $PropertiesFile) {
    Write-Host "✅ Đọc cấu hình từ: $PropertiesFile" -ForegroundColor Green
    
    $properties = Get-Content $PropertiesFile
    
    # Parse connection info
    $url = ($properties | Where-Object {$_ -match "spring.datasource.url"} | ForEach-Object {$_.Split("=")[1]}).Trim()
    $username = ($properties | Where-Object {$_ -match "spring.datasource.username"} | ForEach-Object {$_.Split("=")[1]}).Trim()
    $password = ($properties | Where-Object {$_ -match "spring.datasource.password"} | ForEach-Object {$_.Split("=")[1]}).Trim()
    
    # Parse URL components
    if ($url -match "jdbc:oracle:thin:@(.+):(\d+)/(.+)") {
        $hostname = $matches[1]
        $port = $matches[2]
        $serviceName = $matches[3]
        
        Write-Host "📋 Thông tin kết nối:" -ForegroundColor Cyan
        Write-Host "  Hostname: $hostname" -ForegroundColor White
        Write-Host "  Port: $port" -ForegroundColor White
        Write-Host "  Service Name: $serviceName" -ForegroundColor White
        Write-Host "  Username: $username" -ForegroundColor White
        Write-Host "  Password: $password" -ForegroundColor White
        Write-Host ""
        
        # Tạo connection XML
        $connectionXml = @"
<?xml version = '1.0' encoding = 'UTF-8'?>
<References xmlns="http://xmlns.oracle.com/adf/jndi">
   <Reference name="SpringBoot_ECommerce" className="oracle.jdeveloper.db.adapter.DatabaseProvider" xmlns="">
      <Factory className="oracle.jdeveloper.db.adapter.DatabaseProviderFactory"/>
      <RefAddresses>
         <StringRefAddr addrType="user">
            <Contents>$username</Contents>
         </StringRefAddr>
         <StringRefAddr addrType="subtype">
            <Contents>oraJDBC</Contents>
         </StringRefAddr>
         <StringRefAddr addrType="customUrl">
            <Contents>$url</Contents>
         </StringRefAddr>
         <StringRefAddr addrType="hostname">
            <Contents>$hostname</Contents>
         </StringRefAddr>
         <StringRefAddr addrType="port">
            <Contents>$port</Contents>
         </StringRefAddr>
         <StringRefAddr addrType="serviceName">
            <Contents>$serviceName</Contents>
         </StringRefAddr>
         <StringRefAddr addrType="ConnName">
            <Contents>SpringBoot_ECommerce</Contents>
         </StringRefAddr>
         <StringRefAddr addrType="NoPasswordConnection">
            <Contents>FALSE</Contents>
         </StringRefAddr>
         <StringRefAddr addrType="driver">
            <Contents>oracle.jdbc.OracleDriver</Contents>
         </StringRefAddr>
         <StringRefAddr addrType="password">
            <Contents>$password</Contents>
         </StringRefAddr>
      </RefAddresses>
   </Reference>
</References>
"@
        
        # Lưu file connection
        $connectionFile = "SpringBoot_ECommerce_Connection.xml"
        $connectionXml | Out-File -FilePath $connectionFile -Encoding UTF8
        
        Write-Host "✅ Đã tạo file connection: $connectionFile" -ForegroundColor Green
        Write-Host ""
        
        # Hướng dẫn import
        Write-Host "📥 CÁCH IMPORT VÀO SQL DEVELOPER:" -ForegroundColor Yellow
        Write-Host "1. Mở Oracle SQL Developer" -ForegroundColor White
        Write-Host "2. Right-click vào 'Connections' → 'Import Connections...'" -ForegroundColor White
        Write-Host "3. Chọn file: $connectionFile" -ForegroundColor White
        Write-Host "4. Click 'Open' → 'OK'" -ForegroundColor White
        Write-Host "5. Connection 'SpringBoot_ECommerce' sẽ xuất hiện" -ForegroundColor White
        Write-Host "6. Double-click để kết nối" -ForegroundColor White
        
    } else {
        Write-Host "❌ Không thể parse URL: $url" -ForegroundColor Red
    }
    
} else {
    Write-Host "❌ Không tìm thấy file: $PropertiesFile" -ForegroundColor Red
}

Write-Host ""
Write-Host "🔗 Thông tin kết nối thủ công:" -ForegroundColor Cyan
Write-Host "Connection Name: SpringBoot_ECommerce" -ForegroundColor White
Write-Host "Username: system" -ForegroundColor White
Write-Host "Password: 1234567a@" -ForegroundColor White
Write-Host "Hostname: localhost" -ForegroundColor White
Write-Host "Port: 1521" -ForegroundColor White
Write-Host "Service name: XE" -ForegroundColor White