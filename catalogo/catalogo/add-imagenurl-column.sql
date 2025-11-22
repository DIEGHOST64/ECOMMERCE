-- Agregar columna imagen_url a la tabla item_carrito si no existe
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'item_carrito' 
        AND column_name = 'imagen_url'
    ) THEN
        ALTER TABLE item_carrito ADD COLUMN imagen_url TEXT;
        RAISE NOTICE 'Columna imagen_url agregada a item_carrito';
    ELSE
        RAISE NOTICE 'Columna imagen_url ya existe en item_carrito';
    END IF;
END $$;

-- Verificar que la columna existe
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'item_carrito' 
AND column_name = 'imagen_url';
