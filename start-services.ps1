# Cargar variables de entorno
Get-Content .env | ForEach-Object {
    $line = $_.Trim()
    if ($line -and !$line.StartsWith('#') -and $line.Contains('=')) {
        $parts = $line.Split('=', 2)
        $name = $parts[0].Trim()
        $value = $parts[1].Trim()
        Set-Item -Path "env:$name" -Value $value
        Write-Host "Variable cargada: $name" -ForegroundColor Green
    }
}

Write-Host "`nINICIANDO MICROSERVICIOS`n" -ForegroundColor Cyan

# Detener servicios anteriores
Get-Job | Remove-Job -Force -ErrorAction SilentlyContinue
Get-Process -Name "java" -ErrorAction SilentlyContinue | Stop-Process -Force
Start-Sleep -Seconds 2

# Iniciar AUTH (Puerto 9090)
Write-Host "Iniciando AUTH en puerto 9090..." -ForegroundColor Yellow
Start-Job -Name "AuthService" -ScriptBlock {
    $env:AWS_ACCESS_KEY_ID = $using:env:AWS_ACCESS_KEY_ID
    $env:AWS_SECRET_ACCESS_KEY = $using:env:AWS_SECRET_ACCESS_KEY
    $env:AWS_SQS_QUEUE_URL = $using:env:AWS_SQS_QUEUE_URL
    $env:AWS_REGION = $using:env:AWS_REGION
    $env:DB_HOST = $using:env:DB_HOST
    $env:DB_PORT = $using:env:DB_PORT
    $env:DB_NAME = $using:env:DB_NAME_AUTH
    $env:DB_USERNAME = $using:env:DB_USERNAME
    $env:DB_PASSWORD = $using:env:DB_PASSWORD
    
    cd 'C:\Users\PC\Documents\Proyecto ECCOMERCE\Proyecto ECCOMERCE\Proyecto ECCOMERCE\auth\auth\auth'
    .\mvnw.cmd spring-boot:run
} | Out-Null

Start-Sleep -Seconds 5

# Iniciar CATALOGO (Puerto 9092)
Write-Host "Iniciando CATALOGO en puerto 9092..." -ForegroundColor Yellow
Start-Job -Name "CatalogoService" -ScriptBlock {
    $env:AWS_ACCESS_KEY_ID = $using:env:AWS_ACCESS_KEY_ID
    $env:AWS_SECRET_ACCESS_KEY = $using:env:AWS_SECRET_ACCESS_KEY
    $env:AWS_SQS_QUEUE_URL = $using:env:AWS_SQS_QUEUE_URL
    $env:AWS_REGION = $using:env:AWS_REGION
    $env:DB_HOST = $using:env:DB_HOST
    $env:DB_PORT = $using:env:DB_PORT
    $env:DB_NAME_CATALOG = $using:env:DB_NAME_CATALOG
    $env:DB_USERNAME = $using:env:DB_USERNAME
    $env:DB_PASSWORD = $using:env:DB_PASSWORD
    
    cd 'C:\Users\PC\Documents\Proyecto ECCOMERCE\Proyecto ECCOMERCE\Proyecto ECCOMERCE\catalogo\catalogo'
    .\mvnw.cmd spring-boot:run
} | Out-Null

Start-Sleep -Seconds 5

# Iniciar NOTIFICACIONES (Puerto 9093)
Write-Host "Iniciando NOTIFICACIONES en puerto 9093..." -ForegroundColor Yellow
Start-Job -Name "NotificacionesService" -ScriptBlock {
    $env:AWS_ACCESS_KEY_ID = $using:env:AWS_ACCESS_KEY_ID
    $env:AWS_SECRET_ACCESS_KEY = $using:env:AWS_SECRET_ACCESS_KEY
    $env:AWS_SQS_QUEUE_URL = $using:env:AWS_SQS_QUEUE_URL
    $env:AWS_REGION = $using:env:AWS_REGION
    $env:AWS_SES_FROM_EMAIL = $using:env:AWS_SES_FROM_EMAIL
    $env:TWILIO_ACCOUNT_SID = $using:env:TWILIO_ACCOUNT_SID
    $env:TWILIO_AUTH_TOKEN = $using:env:TWILIO_AUTH_TOKEN
    $env:TWILIO_PHONE_NUMBER = $using:env:TWILIO_PHONE_NUMBER
    
    cd 'C:\Users\PC\Documents\Proyecto ECCOMERCE\Proyecto ECCOMERCE\Proyecto ECCOMERCE\notificaciones'
    .\mvnw.cmd spring-boot:run
} | Out-Null

Write-Host "`nSERVICIOS INICIADOS`n" -ForegroundColor Cyan
Write-Host "Esperando inicializacion..." -ForegroundColor Gray
Start-Sleep -Seconds 20

Write-Host "`nESTADO DE LOS SERVICIOS:`n" -ForegroundColor Cyan
Get-Job | Format-Table -Property Id, Name, State -AutoSize

Write-Host "`nServicios en ejecucion`n" -ForegroundColor Green
