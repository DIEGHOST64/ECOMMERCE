# Despliegue

El runtime soportado es **Docker Compose**. Las configuraciones antiguas de Netlify, Render, Railway y GitHub Pages fueron retiradas porque no reflejan el stack actual (RabbitMQ, SMTP y JWT compartido).

Para desarrollo local consulta [INSTALL.md](./INSTALL.md).

## 1. Requisitos

- Docker Engine 24+ con Docker Compose v2.
- Una máquina Linux con al menos 4 GB de RAM (8 GB recomendado para compilar las imágenes).
- Un dominio y acceso al DNS si vas a exponer la aplicación a Internet.
- Opcional: un proxy inverso con TLS (Caddy o Nginx).

## 2. Variables de entorno

Copia `.env.example` a `.env` y cambia todos los valores sensibles.

```bash
cp .env.example .env
```

| Variable | Uso | Valor por defecto |
| --- | --- | --- |
| `POSTGRES_DB` | Nombre de la base | `ecommerce` |
| `POSTGRES_USER` | Usuario de PostgreSQL | `postgres` |
| `POSTGRES_PASSWORD` | Contraseña de PostgreSQL | cambiar |
| `POSTGRES_PORT` | Puerto publicado de PostgreSQL | `5432` |
| `JPA_DDL_AUTO` | Estrategia de Hibernate | `update` |
| `RABBITMQ_USER` | Usuario de RabbitMQ | `ecommerce` |
| `RABBITMQ_PASSWORD` | Contraseña de RabbitMQ | cambiar |
| `RABBITMQ_PORT` | Puerto AMQP | `5672` |
| `RABBITMQ_MANAGEMENT_PORT` | Consola de RabbitMQ | `15672` |
| `JWT_SECRET` | Firma de los JWT (mínimo 32 caracteres) | cambiar |
| `JWT_EXPIRATION_MINUTES` | Vigencia del token | `1440` |
| `FRONTEND_PORT` | Puerto del frontend | `3000` |
| `AUTH_PORT` | Puerto de Auth | `9090` |
| `CATALOGO_PORT` | Puerto de Catálogo | `9092` |
| `NOTIFICACIONES_PORT` | Puerto de Notificaciones | `9093` |
| `MAILPIT_SMTP_PORT` | SMTP de Mailpit | `1025` |
| `MAILPIT_UI_PORT` | Interfaz web de Mailpit | `8025` |
| `CORS_ALLOWED_ORIGINS` | Orígenes permitidos, separados por coma | `http://localhost:3000` |
| `MAIL_FROM` | Remitente de los correos | `no-reply@ecommerce.local` |
| `MAIL_FROM_NAME` | Nombre del remitente | `E-Commerce` |
| `SMS_PROVIDER` | `log` o `webhook` | `log` |
| `SMS_WEBHOOK_URL` | Endpoint del gateway SMS | vacío |
| `SMS_WEBHOOK_TOKEN` | Token del gateway SMS | vacío |
| `REACT_APP_AUTH_URL` | URL pública de Auth (se compila en el bundle) | `http://localhost:9090` |
| `REACT_APP_CATALOGO_URL` | URL pública de Catálogo | `http://localhost:9092` |
| `REACT_APP_REQUEST_TIMEOUT_MS` | Timeout del frontend | `15000` |

Genera secretos fuertes:

```bash
openssl rand -base64 48   # JWT_SECRET
openssl rand -base64 32   # POSTGRES_PASSWORD
openssl rand -base64 32   # RABBITMQ_PASSWORD
```

`JWT_SECRET` debe ser idéntico para Auth y Catálogo. Compose lo inyecta en ambos desde la misma variable.

## 3. Despliegue en un servidor único

```bash
git clone https://github.com/DIEGHOST64/ECOMMERCE.git
cd ECOMMERCE
cp .env.example .env
nano .env
docker compose up -d --build
docker compose ps
```

Verifica los healthchecks:

```bash
curl http://localhost:9090/actuator/health
curl http://localhost:9092/actuator/health
curl http://localhost:9093/actuator/health
curl http://localhost:3000/health
```

El primer arranque compila las imágenes Maven y npm, por lo que puede tardar varios minutos.

Para seguir los logs:

```bash
docker compose logs -f
docker compose logs -f auth
```

## 4. Exposición pública y TLS

El frontend necesita saber las URLs públicas de Auth y Catálogo **en tiempo de compilación**. Si cambias de dominio, actualiza `.env` y reconstruye el frontend:

```bash
docker compose build frontend
docker compose up -d frontend
```

Ejemplo con tres subdominios:

```env
CORS_ALLOWED_ORIGINS=https://shop.ejemplo.com
REACT_APP_AUTH_URL=https://auth.ejemplo.com
REACT_APP_CATALOGO_URL=https://catalogo.ejemplo.com
```

### Caddy (recomendado)

`Caddyfile`:

```caddyfile
shop.ejemplo.com {
    reverse_proxy localhost:3000
}

auth.ejemplo.com {
    reverse_proxy localhost:9090
}

catalogo.ejemplo.com {
    reverse_proxy localhost:9092
}
```

Caddy obtiene y renueva los certificados TLS automáticamente.

### Nginx

```nginx
server {
    listen 443 ssl;
    server_name shop.ejemplo.com;

    ssl_certificate     /etc/letsencrypt/live/shop.ejemplo.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/shop.ejemplo.com/privkey.pem;

    location / {
        proxy_pass http://127.0.0.1:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Repite el patrón para `auth` y `catalogo`. Los certificados pueden emitirse con Certbot.

## 5. Red y puertos en producción

Compose publica en el host todos los servicios. En un servidor real solo deben ser accesibles el frontend y el proxy inverso. Edita `docker-compose.yml` y elimina o comenta los `ports` de:

- `postgres`
- `rabbitmq`
- `auth`
- `catalogo`
- `notificaciones`
- `mailpit`

Los servicios se comunican entre sí por la red interna de Compose usando los nombres `postgres`, `rabbitmq`, `auth`, etc. No necesitan puertos publicados.

Si necesitas administrar RabbitMQ o PostgreSQL de forma remota, publica el puerto solo en loopback:

```yaml
ports:
  - "127.0.0.1:15672:15672"
```

y accede mediante un túnel SSH. No expongas la consola de RabbitMQ (`guest`/`guest`) ni PostgreSQL a Internet.

## 6. Correo y SMS en producción

### Correo

Mailpit es solo para desarrollo; captura los correos y los muestra en `http://localhost:8025` sin enviarlos.

Para producción, configura un servidor SMTP real en el servicio `notificaciones`:

```yaml
environment:
  MAIL_HOST: smtp.tu-proveedor.com
  MAIL_PORT: 587
  MAIL_USERNAME: usuario
  MAIL_PASSWORD: contraseña
  MAIL_SMTP_AUTH: "true"
  MAIL_STARTTLS: "true"
  MAIL_FROM: no-reply@tudominio.com
```

Puedes usar cualquier SMTP, incluido un servidor autoalojado (Postal, Mailu, Postfix) o un proveedor transaccional.

### SMS

- `SMS_PROVIDER=log`: el SMS se escribe en el log. Útil en desarrollo.
- `SMS_PROVIDER=webhook`: se hace `POST` a `SMS_WEBHOOK_URL` con el token en `Authorization: Bearer <SMS_WEBHOOK_TOKEN>`.

Contrato esperado por el webhook:

```json
{
  "numeroTelefono": "+573001234567",
  "mensaje": "Tu compra #12 ha sido confirmada."
}
```

Cualquier gateway HTTP (Jasmin, Kannel, un gateway Android) que acepte este contrato sirve como reemplazo de Twilio.

## 7. Persistencia y copias de seguridad

Los datos viven en los volúmenes `postgres_data` y `rabbitmq_data`.

Copia de PostgreSQL:

```bash
docker compose exec -T postgres pg_dump -U "$POSTGRES_USER" "$POSTGRES_DB" \
  > backup-$(date +%Y%m%d-%H%M).sql
```

Restauración:

```bash
docker compose exec -T postgres psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" \
  < backup-20261112-1200.sql
```

Programa la copia con `cron` y guárdala fuera del servidor. RabbitMQ es un sistema de mensajería: no es la fuente de verdad; la compra ya está en PostgreSQL.

Eliminar todos los datos:

```bash
docker compose down --volumes
```

## 8. Actualizaciones

```bash
git pull
docker compose build
docker compose up -d
docker compose ps
```

Compose recrea solo los contenedores cuya imagen o configuración cambió. Los volúmenes se conservan.

Para cambios de esquema, `JPA_DDL_AUTO=update` permite que Hibernate agregue columnas, pero no elimina ni renombra datos de forma segura. Antes de adoptar `validate` en producción, añade migraciones con Flyway o Liquibase.

## 9. Escalabilidad

- Auth, Catálogo y Notificaciones son stateless (salvo la sesión JWT, que vive en el navegador), por lo que pueden replicarse detrás de un balanceador.
- Todos los servicios deben compartir el mismo `JWT_SECRET`.
- Catálogo necesita `AUTH_SERVICE_URL` apuntando al balanceador de Auth.
- RabbitMQ distribuye los mensajes entre varios consumidores de `notificaciones.queue`.
- PostgreSQL es el límite principal; para crecer, usa un servidor gestionado, réplicas de lectura o separa las bases por servicio.
- El frontend es estático y puede servirse desde cualquier CDN.

## 10. Operación

| Tarea | Comando |
| --- | --- |
| Estado | `docker compose ps` |
| Logs | `docker compose logs -f [servicio]` |
| Healthcheck | `curl http://localhost:<puerto>/actuator/health` |
| Reiniciar un servicio | `docker compose restart auth` |
| Reconstruir un servicio | `docker compose up -d --build auth` |
| Detener sin borrar datos | `docker compose down` |
| Detener y borrar datos | `docker compose down --volumes` |

## 11. Checklist de producción

- [ ] `.env` fuera de Git y con permisos restringidos (`chmod 600 .env`).
- [ ] `POSTGRES_PASSWORD`, `RABBITMQ_PASSWORD` y `JWT_SECRET` aleatorios y distintos de los de ejemplo.
- [ ] `JWT_SECRET` de al menos 32 caracteres e idéntico en Auth y Catálogo.
- [ ] `CORS_ALLOWED_ORIGINS` limitado al dominio real del frontend.
- [ ] `REACT_APP_AUTH_URL` y `REACT_APP_CATALOGO_URL` con HTTPS y reconstrucción del frontend.
- [ ] PostgreSQL y RabbitMQ sin puertos públicos.
- [ ] Mailpit deshabilitado o no expuesto; SMTP real configurado.
- [ ] Copias de seguridad programadas y probadas.
- [ ] TLS activo y renovación automática.
- [ ] Credenciales antiguas de AWS/Twilio rotadas y eliminadas del historial de Git (ver abajo).

## 12. Credenciales históricas

Versiones anteriores de este repositorio público incluyeron credenciales de AWS y Twilio en el historial de Git. Aunque el `HEAD` actual ya no las contiene:

1. **Rótalas o revócalas** en AWS IAM y Twilio. Considera cualquier clave publicada como comprometida.
2. Purga el historial antes de hacer público el repositorio:

```bash
# Opción A: git filter-repo (recomendado)
git filter-repo --replace-text replacements.txt
git push --force --all
git push --force --tags

# Opción B: BFG Repo-Cleaner
java -jar bfg.jar --replace-text replacements.txt
git reflog expire --expire=now --all
git gc --prune=now --aggressive
git push --force
```

`replacements.txt` contiene los secretos a reemplazar, uno por línea:

```text
AKIA...==>REDACTED
D4de...==>REDACTED
ACe3...==>REDACTED
ba4b...==>REDACTED
```

Tras el `force-push`, avisa a cualquier colaborador de que debe volver a clonar o hacer `git fetch --all && git reset --hard origin/main`.

## 13. Solución de problemas

| Síntoma | Causa probable | Solución |
| --- | --- | --- |
| `JWT_SECRET` requerido | Falta en `.env` | Definir la variable y recrear los contenedores |
| Frontend llama a `localhost` en producción | URLs compiladas con el valor por defecto | Actualizar `.env` y `docker compose build frontend` |
| `401` en todas las llamadas | Secretos JWT distintos entre Auth y Catálogo | Usar el mismo `JWT_SECRET` y reiniciar ambos |
| `403` al crear productos | El usuario no es `ADMIN` o el token es anterior al cambio de rol | Actualizar el rol en PostgreSQL y volver a iniciar sesión |
| Error de CORS | Origen no incluido en `CORS_ALLOWED_ORIGINS` | Añadir el origen exacto (esquema + dominio + puerto) |
| RabbitMQ rechaza la conexión | Se está usando `guest` desde otro contenedor | Usar `RABBITMQ_USER`/`RABBITMQ_PASSWORD` no `guest` |
| No llegan correos | Mailpit no está corriendo o el SMTP está mal configurado | Revisar `docker compose logs notificaciones` y `http://localhost:8025` |
| Healthcheck en `starting` | El servicio aún compila/arranca | Esperar y revisar `docker compose logs` |
| PostgreSQL no arranca | Contraseña o volumen incompatible | Revisar logs; en desarrollo se puede usar `down --volumes` (borra datos) |
