# Instalación

Hay dos formas de ejecutar el proyecto:

1. **Docker Compose (recomendado):** levanta frontend, backends, PostgreSQL, RabbitMQ y Mailpit con un comando.
2. **Desarrollo local:** la infraestructura corre en Docker y cada servicio se ejecuta con Maven o npm para poder depurar.

## Requisitos

### Opción Docker

- Docker Desktop (Windows/macOS) o Docker Engine + Docker Compose v2 (Linux).
- 4 GB de RAM libres como mínimo; 8 GB recomendado para compilar.

Verifica:

```bash
docker --version
docker compose version
```

### Opción local

- JDK 17 (`java -version`).
- Node.js 20 y npm (`node --version`, `npm --version`).
- Docker para PostgreSQL, RabbitMQ y Mailpit (o instalaciones locales equivalentes).
- Git.

El proyecto incluye Maven Wrapper (`mvnw`/`mvnw.cmd`); no necesitas instalar Maven globalmente.

---

## Opción A: Docker Compose (recomendado)

### 1. Clonar

```bash
git clone https://github.com/DIEGHOST64/ECOMMERCE.git
cd ECOMMERCE
```

### 2. Crear el archivo de entorno

```bash
cp .env.example .env
```

En Windows:

```cmd
copy .env.example .env
```

Edita `.env` y cambia como mínimo:

```env
POSTGRES_PASSWORD=una-clave-larga-y-aleatoria
RABBITMQ_PASSWORD=otra-clave-larga-y-aleatoria
JWT_SECRET=un-secreto-de-al-menos-32-caracteres
```

Genera valores con:

```bash
openssl rand -base64 48
```

En Windows sin OpenSSL puedes usar PowerShell:

```powershell
[Convert]::ToBase64String((1..48 | ForEach-Object { Get-Random -Maximum 256 }))
```

### 3. Arrancar

Linux/macOS:

```bash
./start.sh
```

Windows:

```powershell
powershell -ExecutionPolicy Bypass -File .\start-services.ps1
```

O directamente:

```bash
docker compose up -d --build
```

El primer arranque compila las imágenes y puede tardar varios minutos.

### 4. Verificar

```bash
docker compose ps
curl http://localhost:9090/actuator/health
curl http://localhost:9092/actuator/health
curl http://localhost:9093/actuator/health
curl http://localhost:3000/health
```

Todos deben responder `{"status":"UP"}` o `ok`.

Abre:

| Servicio | URL |
| --- | --- |
| Tienda | http://localhost:3000 |
| Auth API | http://localhost:9090 |
| Catálogo API | http://localhost:9092 |
| Mailpit (correos) | http://localhost:8025 |
| RabbitMQ Management | http://localhost:15672 |

Las credenciales de RabbitMQ son `RABBITMQ_USER` / `RABBITMQ_PASSWORD` del `.env`.

### 5. Crear un administrador

El registro público siempre crea usuarios `USER`. Para ascender al primer administrador:

```bash
docker compose exec postgres psql -U postgres -d ecommerce \
  -c "UPDATE usuario SET role = 'ADMIN' WHERE email = 'tu@email.com';"
```

En Windows, si el comando multilínea falla:

```powershell
docker compose exec postgres psql -U postgres -d ecommerce -c "UPDATE usuario SET role = 'ADMIN' WHERE email = 'tu@email.com';"
```

Cierra sesión y vuelve a entrar para que el nuevo JWT incluya el rol.

### 6. Detener

```bash
docker compose down            # conserva los datos
docker compose down --volumes  # elimina PostgreSQL y RabbitMQ
```

En Windows:

```powershell
.\stop-services.ps1
.\stop-services.ps1 -Volumes   # borra los datos
```

---

## Opción B: desarrollo local

La infraestructura corre en contenedores y los servicios en tu máquina.

### 1. Preparar el entorno

Copia `.env.example` a `.env` y edita los secretos (igual que en la opción A).

### 2. Levantar solo la infraestructura

```bash
docker compose up -d postgres rabbitmq mailpit
```

El script `run-local.ps1` hace esto automáticamente y traduce las variables del `.env` raíz a las que espera cada servicio.

### 3. Ejecutar un servicio

En una terminal por servicio:

```powershell
.\run-local.ps1 auth
.\run-local.ps1 catalogo
.\run-local.ps1 notificaciones
```

Alternativa manual (Linux/macOS):

```bash
# Auth
cd auth/auth/auth
./mvnw spring-boot:run

# Catálogo
cd catalogo/catalogo
./mvnw spring-boot:run

# Notificaciones
cd notificaciones
./mvnw spring-boot:run
```

En Windows usa `mvnw.cmd`.

Si ejecutas Maven manualmente, exporta las variables del `.env` raíz o crea un `.env` dentro de cada servicio. Recuerda que:

- `DB_URL` debe apuntar a `jdbc:postgresql://localhost:5432/ecommerce`.
- `DB_USERNAME` y `DB_PASSWORD` deben coincidir con PostgreSQL.
- `RABBITMQ_USERNAME` y `RABBITMQ_PASSWORD` deben coincidir con RabbitMQ (no uses `guest` si RabbitMQ corre en otro contenedor).
- `JWT_SECRET` debe ser el mismo en Auth y Catálogo.

### 4. Ejecutar el frontend

```bash
cd frontend
npm ci
npm start
```

Abre http://localhost:3000. Por defecto llama a `http://localhost:9090` y `http://localhost:9092`. Para cambiarlo crea `frontend/.env.local`:

```env
REACT_APP_AUTH_URL=http://localhost:9090
REACT_APP_CATALOGO_URL=http://localhost:9092
REACT_APP_REQUEST_TIMEOUT_MS=15000
```

---

## Opción C: todo local sin Docker

Necesitas instalar y configurar por tu cuenta:

- PostgreSQL 16 con una base llamada `ecommerce`.
- RabbitMQ 3.13.
- Un servidor SMTP (por ejemplo Mailpit) o credenciales SMTP reales.

Luego exporta las variables de los `.env.example` y arranca los tres servicios con Maven y el frontend con npm.

No es la ruta recomendada porque la configuración de RabbitMQ, SMTP y PostgreSQL es propensa a diferencias entre sistemas operativos.

---

## Pruebas

### Backend

```bash
cd auth/auth/auth && ./mvnw clean verify
cd catalogo/catalogo && ./mvnw clean verify
cd notificaciones && ./mvnw clean verify
```

En Windows:

```powershell
cd auth\auth\auth
cmd /c "mvnw.cmd clean verify"

cd ..\..\..\catalogo\catalogo
cmd /c "mvnw.cmd clean verify"

cd ..\..\notificaciones
cmd /c "mvnw.cmd clean verify"
```

Resultados esperados: 19 tests en Auth, 20 en Catálogo y 16 en Notificaciones.

### Frontend

```bash
cd frontend
npm test -- --watchAll=false
npm run build
```

Resultado esperado: 12 tests y build de producción sin warnings.

### Integración continua

`.github/workflows/ci.yml` ejecuta las tres suites Maven, las pruebas y el build del frontend, y valida `docker-compose.yml`.

---

## Problemas comunes

### `JWT_SECRET` debe tener al menos 32 caracteres

El servicio Auth no arranca si el secreto es más corto. Genera uno con `openssl rand -base64 48` y actualiza `.env`.

### `401` en todas las peticiones protegidas

- Auth y Catálogo deben usar el mismo `JWT_SECRET`.
- El token pudo expirar; vuelve a iniciar sesión.
- El header debe ser `Authorization: Bearer <token>`.

### `403` al crear o editar productos

El usuario no tiene rol `ADMIN`, o el token se emitió antes de cambiar el rol. Actualiza el rol en PostgreSQL y vuelve a iniciar sesión.

### RabbitMQ rechaza `guest`

`guest` solo puede conectarse desde loopback. Usa el usuario `RABBITMQ_USER` definido en `.env`.

### El frontend no conecta con el backend

- Verifica que Auth (9090) y Catálogo (9092) estén arriba.
- Revisa `CORS_ALLOWED_ORIGINS`; debe incluir el origen exacto del frontend.
- En Docker, las URLs del frontend se compilan en el bundle. Si cambias `.env`, ejecuta `docker compose build frontend`.

### Puerto ocupado

Cambia los puertos en `.env` (`FRONTEND_PORT`, `AUTH_PORT`, `CATALOGO_PORT`, etc.) y reinicia. Para el frontend local, `PORT=3001 npm start`.

### PostgreSQL no arranca

Revisa `docker compose logs postgres`. Si el volumen es de una versión incompatible y no necesitas los datos, usa `docker compose down --volumes`.

### No llegan correos

En desarrollo, Mailpit debe estar arriba: `docker compose up -d mailpit` y revisa http://localhost:8025. En producción, revisa las variables `MAIL_*` y los logs de `notificaciones`.

### Los SMS no se envían

Con `SMS_PROVIDER=log` solo se escriben en el log. Para envío real configura `SMS_PROVIDER=webhook`, `SMS_WEBHOOK_URL` y, si aplica, `SMS_WEBHOOK_TOKEN`.

Para despliegue en servidor consulta [DEPLOYMENT.md](./DEPLOYMENT.md).
