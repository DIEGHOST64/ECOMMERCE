# Guía de Inicio de Microservicios E-Commerce

## Prerrequisitos

### 1. PostgreSQL
Los microservicios **auth** y **catalogo** requieren PostgreSQL.

**Verificar si PostgreSQL está instalado:**
```cmd
psql --version
```

**Si NO está instalado, descárgalo desde:**
https://www.postgresql.org/download/windows/

**Configuración predeterminada esperada:**
- Host: `localhost`
- Puerto: `5432`
- Usuario: `postgres`
- Contraseña: `admin`
- Bases de datos: 
  - `ecommerce_auth` (para auth)
  - `ecommerce` (para catalogo)

**Crear las bases de datos:**
```sql
CREATE DATABASE ecommerce_auth;
CREATE DATABASE ecommerce;
```

### 2. Java
Todos los microservicios requieren Java 17 o superior.

```cmd
java -version
```

## Orden de Inicio de Microservicios

### 1. Auth (Puerto 9090)
```cmd
cd "c:\Users\PC\Documents\Proyecto ECCOMERCE\Proyecto ECCOMERCE\Proyecto ECCOMERCE\auth\auth\auth"
.\mvnw.cmd spring-boot:run
```

### 2. Catalogo (Puerto 9092)
```cmd
cd "c:\Users\PC\Documents\Proyecto ECCOMERCE\Proyecto ECCOMERCE\Proyecto ECCOMERCE\catalogo\catalogo"
.\mvnw.cmd spring-boot:run
```

### 3. Notificaciones (Puerto 9093)
```cmd
cd "c:\Users\PC\Documents\Proyecto ECCOMERCE\Proyecto ECCOMERCE\Proyecto ECCOMERCE\notificaciones"
.\mvnw.cmd spring-boot:run
```

## Scripts Automáticos

Ya tienes scripts PowerShell para iniciar todos los servicios:

**Iniciar todos los servicios:**
```cmd
powershell -ExecutionPolicy Bypass -File "c:\Users\PC\Documents\Proyecto ECCOMERCE\Proyecto ECCOMERCE\Proyecto ECCOMERCE\start-services.ps1"
```

**Detener todos los servicios:**
```cmd
powershell -ExecutionPolicy Bypass -File "c:\Users\PC\Documents\Proyecto ECCOMERCE\Proyecto ECCOMERCE\Proyecto ECCOMERCE\stop-services.ps1"
```

## Configuración Opcional (AWS y Twilio)

Si necesitas funcionalidades de notificaciones completas, configura estas variables de entorno:

### AWS (para SQS y SES):
```cmd
set AWS_REGION=us-east-1
set AWS_ACCESS_KEY_ID=tu_access_key
set AWS_SECRET_ACCESS_KEY=tu_secret_key
set AWS_SQS_QUEUE_URL=tu_queue_url
set AWS_SES_FROM_EMAIL=tu_email@ejemplo.com
```

### Twilio (para SMS):
```cmd
set TWILIO_ACCOUNT_SID=tu_account_sid
set TWILIO_AUTH_TOKEN=tu_auth_token
set TWILIO_PHONE_NUMBER=tu_numero
```

## Verificar que los servicios están corriendo

- Auth: http://localhost:9090
- Catalogo: http://localhost:9092
- Notificaciones: http://localhost:9093

## Problemas Comunes

### Error de conexión a PostgreSQL
- Verifica que PostgreSQL esté corriendo
- Confirma usuario/contraseña en pgAdmin o psql
- Asegúrate que las bases de datos existan

### Puerto ya en uso
- Cierra otras aplicaciones que usen los puertos 9090, 9092, 9093
- O modifica `server.port` en `application.properties`

### Error de compilación
```cmd
.\mvnw.cmd clean install
```
