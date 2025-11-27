@echo off
echo ========================================
echo  E-Commerce - Inicio Rapido
echo ========================================
echo.

echo [1/4] Verificando PostgreSQL...
psql -U postgres -c "SELECT version();" >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] PostgreSQL no esta corriendo o no esta instalado
    echo Por favor inicia PostgreSQL e intenta de nuevo
    pause
    exit /b 1
)
echo [OK] PostgreSQL esta corriendo

echo.
echo [2/4] Verificando Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java no esta instalado
    echo Descarga Java 17+ desde: https://adoptium.net/
    pause
    exit /b 1
)
echo [OK] Java esta instalado

echo.
echo [3/4] Verificando Node.js...
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Node.js no esta instalado
    echo Descarga Node.js desde: https://nodejs.org/
    pause
    exit /b 1
)
echo [OK] Node.js esta instalado

echo.
echo [4/4] Verificando archivos .env...
if not exist "auth\auth\auth\.env" (
    echo [ADVERTENCIA] No existe auth\.env - creando desde ejemplo...
    copy "auth\auth\auth\.env.example" "auth\auth\auth\.env" >nul 2>&1
    echo [!] Por favor edita auth\auth\auth\.env con tus credenciales
)
if not exist "catalogo\catalogo\.env" (
    echo [ADVERTENCIA] No existe catalogo\.env - creando desde ejemplo...
    copy "catalogo\catalogo\.env.example" "catalogo\catalogo\.env" >nul 2>&1
    echo [!] Por favor edita catalogo\catalogo\.env con tus credenciales
)

echo.
echo ========================================
echo  Verificacion completada!
echo ========================================
echo.
echo Para iniciar los servicios:
echo   1. Auth:     cd auth\auth\auth ^&^& mvnw.cmd spring-boot:run
echo   2. Catalogo: cd catalogo\catalogo ^&^& mvnw.cmd spring-boot:run
echo   3. Frontend: cd frontend ^&^& npm start
echo.
echo O usa: start-services.ps1 (PowerShell)
echo.
pause
