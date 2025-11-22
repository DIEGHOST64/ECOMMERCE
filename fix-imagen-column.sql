-- Primero eliminar la columna imagen_url si existe
ALTER TABLE products DROP COLUMN IF EXISTS imagen_url;

-- Agregar la columna imagen_url como TEXT
ALTER TABLE products ADD COLUMN imagen_url TEXT;

-- Verificar estructura de la tabla
SELECT column_name, data_type, character_maximum_length 
FROM information_schema.columns 
WHERE table_name = 'products';
