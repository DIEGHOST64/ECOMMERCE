@echo off
setlocal
cd /d "%~dp0"

if not exist ".env" (
    echo [ERROR] No existe el archivo .env
    echo Ejecuta setup.bat o copia .env.example a .env y edita los secretos.
    pause
    exit /b 1
)

docker compose version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker Compose v2 no esta disponible.
    echo Instala Docker Desktop y vuelve a intentarlo.
    pause
    exit /b 1
)

echo Iniciando AUTH y sus dependencias...
docker compose up -d --build auth
if errorlevel 1 pause
