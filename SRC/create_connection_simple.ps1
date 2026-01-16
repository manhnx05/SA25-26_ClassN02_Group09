# Script đơn giản tạo Oracle SQL Developer Connection

Write-Host "=== ORACLE SQL DEVELOPER CONNECTION CREATOR ===" -ForegroundColor Green
Write-Host ""

# Đọc thông tin từ application.properties
$propertiesFile = "src/main/resources/application.properties"

if (Test-Path $propertiesFile) {
    Write-Host "Reading configuration from: $propertiesFile" -ForegroundColor Green
    
    # Tạo connection XML với thông tin cố định
    $connectionXml = @'
<?xml version = '1.0' encoding = 'UTF-8'?>
<References xmlns="http://xmlns.oracle.com/adf/jndi">
   <Reference name="SpringBoot_ECommerce" className="oracle.jdeveloper.db.adapter.DatabaseProvider" xmlns="">
      <Factory className="oracle.jdeveloper.db.adapter.DatabaseProviderFactory"/>
      <RefAddresses>
         <StringRefAddr addrType="user">
            <Contents>system</Contents>
         </StringRefAddr>
         <StringRefAddr addrType="subtype">
            <Contents>oraJDBC</Contents>
         </StringRefAddr>
         <StringRefAddr addrType="customUrl">
            <Contents>jdbc:oracle:thin:@localhost:1521/XE</Contents>
         </StringRefAddr>
         <StringRefAddr addrType="hostname">
            <Contents>localhost</Contents>
         </StringRefAddr>
         <StringRefAddr addrType="port">
            <Contents>1521</Contents>
         </StringRefAddr>
         <StringRefAddr addrType="serviceName">
            <Contents>XE</Contents>
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
      </RefAddresses>
   </Reference>
</References>
'@
    
    # Lưu file connection
    $connectionFile = "SpringBoot_ECommerce_Connection.xml"
    $connectionXml | Out-File -FilePath $connectionFile -Encoding UTF8
    
    Write-Host "Created connection file: $connectionFile" -ForegroundColor Green
    Write-Host ""
    
    Write-Host "HOW TO IMPORT INTO SQL DEVELOPER:" -ForegroundColor Yellow
    Write-Host "1. Open Oracle SQL Developer" -ForegroundColor White
    Write-Host "2. Right-click on 'Connections' -> 'Import Connections...'" -ForegroundColor White
    Write-Host "3. Select file: $connectionFile" -ForegroundColor White
    Write-Host "4. Click 'Open' -> 'OK'" -ForegroundColor White
    Write-Host "5. Connection 'SpringBoot_ECommerce' will appear" -ForegroundColor White
    Write-Host "6. Double-click to connect (password: 1234567a@)" -ForegroundColor White
    
} else {
    Write-Host "File not found: $propertiesFile" -ForegroundColor Red
}

Write-Host ""
Write-Host "Manual Connection Info:" -ForegroundColor Cyan
Write-Host "Connection Name: SpringBoot_ECommerce" -ForegroundColor White
Write-Host "Username: system" -ForegroundColor White
Write-Host "Password: 1234567a@" -ForegroundColor White
Write-Host "Hostname: localhost" -ForegroundColor White
Write-Host "Port: 1521" -ForegroundColor White
Write-Host "Service name: XE" -ForegroundColor White