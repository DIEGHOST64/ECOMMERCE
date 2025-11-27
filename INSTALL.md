# Guía de Instalación Rápida

## Pasos para ejecutar el proyecto

### 1. Instalar Prerrequisitos

- **Java 17+**: https://adoptium.net/
- **PostgreSQL**: https://www.postgresql.org/download/
- **Node.js 16+**: https://nodejs.org/

### 2. Configurar Base de Datos

```cmd
psql -U postgres
CREATE DATABASE ecommerce_auth;
CREATE DATABASE ecommerce;
\q
```

### 3. Configurar Variables de Entorno

Copia los archivos de ejemplo y configura tus credenciales:

```cmd
copy auth\auth\auth\.env.example auth\auth\auth\.env
copy catalogo\catalogo\.env.example catalogo\catalogo\.env
copy notificaciones\.env.example notificaciones\.env
```

Edita cada archivo `.env` con tus credenciales de PostgreSQL.

### 4. Instalar Dependencias Frontend

```cmd
cd frontend
npm install
cd ..
```

### 5. Ejecutar los Servicios

**Opción A - Automático (PowerShell)**:
```cmd
.\start-services.ps1
```

**Opción B - Manual**:

Terminal 1 - Auth:
```cmd
cd auth\auth\auth
mvnw.cmd spring-boot:run
```

Terminal 2 - Catálogo:
```cmd
cd catalogo\catalogo
mvnw.cmd spring-boot:run
```

Terminal 3 - Frontend:
```cmd
cd frontend
npm start
```

### 6. Acceder a la Aplicación

- Frontend: http://localhost:3000
- Auth API: http://localhost:9090
- Catálogo API: http://localhost:9092

## Problemas Comunes

**Error de conexión a BD**: Verifica que PostgreSQL esté corriendo y las credenciales en `.env` sean correctas.

**Puerto en uso**: Asegúrate de que los puertos 9090, 9092 y 3000 estén disponibles.

Ver `README.md` para documentación completa.
