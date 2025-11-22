-- Agregar columna imagen_url a la tabla products
ALTER TABLE products ADD COLUMN IF NOT EXISTS imagen_url TEXT;

-- Ver las columnas de la tabla products
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'products';
