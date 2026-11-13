@echo off
setlocal
cd /d "%~dp0"

echo ========================================
echo  E-Commerce - Verificacion inicial
echo ========================================
echo.

where docker >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker no esta instalado.
    echo Descarga Docker Desktop: https://www.docker.com/products/docker-desktop/
    pause
    exit /b 1
)
echo [OK] Docker esta instalado

docker compose version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker Compose v2 no esta disponible.
    pause
    exit /b 1
)
echo [OK] Docker Compose esta disponible

if not exist ".env" (
    copy /Y ".env.example" ".env" >nul
    echo.
    echo [IMPORTANTE] Se creo el archivo .env.
    echo Editalo y cambia POSTGRES_PASSWORD, RABBITMQ_PASSWORD y JWT_SECRET.
    echo Luego ejecuta: start-services.ps1
    echo.
    pause
    exit /b 0
)

echo [OK] El archivo .env existe
echo.
echo Para iniciar la plataforma ejecuta:
echo   powershell -ExecutionPolicy Bypass -File .\start-services.ps1
echo.
pause
