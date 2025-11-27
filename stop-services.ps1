# ========================================
# SCRIPT PARA DETENER TODOS LOS MICROSERVICIOS
# ========================================

Write-Host "`n========================================" -ForegroundColor Red
Write-Host "DETENIENDO MICROSERVICIOS" -ForegroundColor Red
Write-Host "========================================`n" -ForegroundColor Red

# Detener jobs de PowerShell
$jobs = Get-Job -ErrorAction SilentlyContinue
if ($jobs) {
    Write-Host "🛑 Deteniendo jobs de PowerShell..." -ForegroundColor Yellow
    Get-Job | Stop-Job
    Get-Job | Remove-Job -Force
    Write-Host "✓ Jobs detenidos" -ForegroundColor Green
} else {
    Write-Host "ℹ No hay jobs en ejecución" -ForegroundColor Gray
}

# Detener procesos Java
$javaProcesses = Get-Process -Name "java" -ErrorAction SilentlyContinue
if ($javaProcesses) {
    Write-Host "🛑 Deteniendo procesos Java..." -ForegroundColor Yellow
    $javaProcesses | Stop-Process -Force
    Write-Host "✓ Procesos Java detenidos" -ForegroundColor Green
} else {
    Write-Host "ℹ No hay procesos Java en ejecución" -ForegroundColor Gray
}

Start-Sleep -Seconds 2

Write-Host "`n✅ Todos los servicios han sido detenidos`n" -ForegroundColor Green
