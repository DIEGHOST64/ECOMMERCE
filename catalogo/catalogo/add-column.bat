@echo off
echo Agregando columna imagen_url a item_carrito...
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -d ecommerce -f add-imagenurl-column.sql
if %ERRORLEVEL% EQU 0 (
    echo.
    echo Columna agregada exitosamente!
) else (
    echo.
    echo Error al agregar la columna
)
pause
