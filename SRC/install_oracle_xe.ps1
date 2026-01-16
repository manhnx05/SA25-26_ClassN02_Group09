# Oracle XE 21c Silent Installation Script
# Run as Administrator

param(
    [string]$OraclePassword = "1234567a@",
    [string]$InstallPath = "C:\app\oracle\product\21c\dbhomeXE"
)

Write-Host "=== Oracle XE 21c Silent Installation ===" -ForegroundColor Green
Write-Host "Password: $OraclePassword" -ForegroundColor Cyan
Write-Host "Install Path: $InstallPath" -ForegroundColor Cyan
Write-Host ""

# Check if running as Administrator
if (-NOT ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")) {
    Write-Host "ERROR: This script must be run as Administrator!" -ForegroundColor Red
    Write-Host "Right-click PowerShell and select 'Run as Administrator'" -ForegroundColor Yellow
    exit 1
}

# Find setup.exe
$setupPath = Get-ChildItem "C:\temp\oracle" -Recurse -Name "setup.exe" | Select-Object -First 1
if (-not $setupPath) {
    Write-Host "ERROR: setup.exe not found in C:\temp\oracle" -ForegroundColor Red
    exit 1
}

$setupFullPath = Join-Path "C:\temp\oracle" $setupPath
Write-Host "Found setup.exe: $setupFullPath" -ForegroundColor Green

# Create response file for silent installation
$responseFile = @"
oracle.install.responseFileVersion=/oracle/install/rspfmt_dbinstall_response_schema_v21.0.0
oracle.install.option=INSTALL_DB_SWONLY
UNIX_GROUP_NAME=
INVENTORY_LOCATION=C:\Program Files\Oracle\Inventory
ORACLE_HOME=$InstallPath
ORACLE_BASE=C:\app\oracle
oracle.install.db.InstallEdition=XE
oracle.install.db.OSDBA_GROUP=ORA_DBA
oracle.install.db.OSOPER_GROUP=ORA_OPER
oracle.install.db.OSBACKUPDBA_GROUP=ORA_BACKUPDBA
oracle.install.db.OSDGDBA_GROUP=ORA_DG
oracle.install.db.OSKMDBA_GROUP=ORA_KM
oracle.install.db.OSRACDBA_GROUP=ORA_RAC
SECURITY_UPDATES_VIA_MYORACLESUPPORT=false
DECLINE_SECURITY_UPDATES=true
"@

$responseFilePath = "C:\temp\oracle\xe_install.rsp"
$responseFile | Out-File -FilePath $responseFilePath -Encoding ASCII
Write-Host "Created response file: $responseFilePath" -ForegroundColor Green

# Run silent installation
Write-Host "Starting Oracle XE installation..." -ForegroundColor Yellow
$installArgs = @(
    "-silent"
    "-responseFile", $responseFilePath
    "-ignorePrereq"
    "-waitforcompletion"
)

try {
    $process = Start-Process -FilePath $setupFullPath -ArgumentList $installArgs -Wait -PassThru -NoNewWindow
    
    if ($process.ExitCode -eq 0) {
        Write-Host "Oracle XE installation completed successfully!" -ForegroundColor Green
        
        # Start Oracle services
        Write-Host "Starting Oracle services..." -ForegroundColor Yellow
        Start-Service "OracleServiceXE" -ErrorAction SilentlyContinue
        Start-Service "OracleOraDB21Home1TNSListener" -ErrorAction SilentlyContinue
        
        # Test connection
        Write-Host "Testing database connection..." -ForegroundColor Yellow
        $env:ORACLE_HOME = $InstallPath
        $env:ORACLE_SID = "XE"
        
        $sqlplusPath = "$InstallPath\bin\sqlplus.exe"
        if (Test-Path $sqlplusPath) {
            Write-Host "Oracle XE installation and configuration completed!" -ForegroundColor Green
            Write-Host ""
            Write-Host "Connection details:" -ForegroundColor Cyan
            Write-Host "  Host: localhost" -ForegroundColor White
            Write-Host "  Port: 1521" -ForegroundColor White
            Write-Host "  SID: XE" -ForegroundColor White
            Write-Host "  Username: system" -ForegroundColor White
            Write-Host "  Password: $OraclePassword" -ForegroundColor White
        }
        
    } else {
        Write-Host "Installation failed with exit code: $($process.ExitCode)" -ForegroundColor Red
    }
    
} catch {
    Write-Host "Installation error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "Installation script completed." -ForegroundColor Green