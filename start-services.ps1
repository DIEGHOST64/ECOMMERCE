[CmdletBinding()]
param(
    [switch]$NoBuild,
    [switch]$Logs
)

$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    throw 'Docker no esta instalado. Descarga Docker Desktop: https://www.docker.com/products/docker-desktop/'
}

docker compose version 2>$null | Out-Null
if ($LASTEXITCODE -ne 0) {
    throw 'Docker Compose v2 no esta disponible. Actualiza Docker Desktop.'
}

if (-not (Test-Path '.env')) {
    Copy-Item '.env.example' '.env'
    Write-Warning 'Se creo .env desde .env.example. Edita los secretos y ejecuta de nuevo este script.'
    exit 1
}

$composeArgs = @('compose', 'up', '-d')
if (-not $NoBuild) {
    $composeArgs += '--build'
}

Write-Host 'Iniciando la plataforma...' -ForegroundColor Cyan
& docker @composeArgs
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

& docker compose ps

Write-Host ''
Write-Host 'Servicios disponibles:' -ForegroundColor Green
Write-Host '  Frontend:       http://localhost:3000'
Write-Host '  Auth API:       http://localhost:9090'
Write-Host '  Catalogo API:   http://localhost:9092'
Write-Host '  Mailpit (email): http://localhost:8025'
Write-Host '  RabbitMQ:       http://localhost:15672'
Write-Host ''
Write-Host 'Detener todo: .\stop-services.ps1' -ForegroundColor Gray

if ($Logs) {
    & docker compose logs -f
}
