#!/bin/bash

# Script para configurar variables de entorno desde la URL de PostgreSQL de Render
# Render proporciona DATABASE_URL en formato: postgresql://user:password@host:port/dbname

if [ -n "$DATABASE_URL" ]; then
    # Extraer componentes de la URL
    export DB_HOST=$(echo $DATABASE_URL | sed -e 's/.*@\(.*\):.*/\1/')
    export DB_PORT=$(echo $DATABASE_URL | sed -e 's/.*:\([0-9]*\)\/.*/\1/')
    export DB_NAME=$(echo $DATABASE_URL | sed -e 's/.*\/\(.*\)/\1/')
    export DB_USERNAME=$(echo $DATABASE_URL | sed -e 's/.*:\/\/\(.*\):.*/\1/')
    export DB_PASSWORD=$(echo $DATABASE_URL | sed -e 's/.*:\/\/.*:\(.*\)@.*/\1/')
    
    echo "Database configuration loaded from DATABASE_URL"
fi

# Ejecutar el comando pasado como argumentos
exec "$@"
