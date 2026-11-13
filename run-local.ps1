[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('auth', 'catalogo', 'notificaciones')]
    [string]$Service
)

$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot

if (-not (Test-Path '.env')) {
    throw 'No existe .env. Copia .env.example a .env y edita los secretos.'
}

# Carga el .env raiz en el proceso actual.
Get-Content '.env' | ForEach-Object {
    $line = $_.Trim()
    if ($line -and -not $line.StartsWith('#') -and $line.Contains('=')) {
        $parts = $line -split '=', 2
        Set-Item -Path ("env:" + $parts[0].Trim()) -Value $parts[1].Trim()
    }
}

Write-Host 'Iniciando PostgreSQL, RabbitMQ y Mailpit...' -ForegroundColor Cyan
& docker compose up -d postgres rabbitmq mailpit
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

# Traduce las variables del compose a las que espera cada servicio.
$env:DB_URL = "jdbc:postgresql://localhost:$($env:POSTGRES_PORT)/$($env:POSTGRES_DB)"
$env:DB_USERNAME = $env:POSTGRES_USER
$env:DB_PASSWORD = $env:POSTGRES_PASSWORD
$env:RABBITMQ_HOST = 'localhost'
$env:RABBITMQ_USERNAME = $env:RABBITMQ_USER
$env:RABBITMQ_PASSWORD = $env:RABBITMQ_PASSWORD
$env:AUTH_SERVICE_URL = 'http://localhost:9090'
$env:MAIL_HOST = 'localhost'
$env:MAIL_PORT = $env:MAILPIT_SMTP_PORT
$env:MAIL_USERNAME = ''
$env:MAIL_PASSWORD = ''
$env:MAIL_SMTP_AUTH = 'false'
$env:MAIL_STARTTLS = 'false'

$rutas = @{
    auth = 'auth\auth\auth'
    catalogo = 'catalogo\catalogo'
    notificaciones = 'notificaciones'
}

Push-Location $rutas[$Service]
try {
    Write-Host "Ejecutando $Service con Maven..." -ForegroundColor Green
    & .\mvnw.cmd spring-boot:run
} finally {
    Pop-Location
}
