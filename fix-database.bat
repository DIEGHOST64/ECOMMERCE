@echo off
echo Modificando columna imagen_url en la base de datos...
psql -U postgres -d ecommerce -f fix-imagen-column.sql
pause
