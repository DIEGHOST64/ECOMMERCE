[CmdletBinding()]
param(
    [switch]$Volumes
)

$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    throw 'Docker no esta instalado.'
}

$composeArgs = @('compose', 'down')
if ($Volumes) {
    $composeArgs += '--volumes'
    Write-Warning 'Se eliminaran tambien los volumenes de PostgreSQL y RabbitMQ.'
}

& docker @composeArgs
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

Write-Host 'Servicios detenidos.' -ForegroundColor Green
