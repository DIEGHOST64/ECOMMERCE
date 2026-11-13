# Changelog

Todos los cambios relevantes del proyecto. El formato sigue [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y el versionado [Semantic Versioning](https://semver.org/lang/es/).

## [2.0.0] - 2026-11-12

Refactorización completa para eliminar AWS y Twilio, aplicar Clean Architecture y corregir los flujos que fallaban.

### Añadido

- RabbitMQ como reemplazo de AWS SQS, con exchange topic, cola durable, reintentos y DLQ.
- SMTP (Mailpit en desarrollo) como reemplazo de AWS SES.
- Adaptador SMS por webhook HTTP y modo `log` como reemplazo de Twilio.
- JWT real firmado con HMAC-SHA256 y validado con Spring Security en Auth y Catálogo.
- Spring Boot Actuator y healthchecks de Docker en los tres servicios.
- `docker-compose.yml` con PostgreSQL, RabbitMQ, Mailpit, los tres backends y el frontend.
- Dockerfiles multi-stage para los tres servicios y el frontend, con usuario no root.
- `nginx.conf` para el frontend con fallback de SPA, caché de estáticos y endpoint `/health`.
- `.env.example` raíz y por servicio, sin secretos reales.
- Workflow de GitHub Actions que ejecuta las tres suites Maven, las pruebas y el build del frontend, y valida Compose.
- `run-local.ps1` para desarrollo local con Maven contra la infraestructura de Compose.
- `stop.sh` y scripts de arranque multiplataforma.
- Pruebas del frontend para sesión JWT, normalización de roles y cliente HTTP.
- Documentación nueva: arquitectura, API, instalación, despliegue, dependencias, Postman y contribución.

### Cambiado

- Estructura de los tres servicios alineada con Clean Architecture: `domain`, `application` y `infraestructura`.
- Puertos de entrada y salida explícitos (`port/in`, `port/out`) y wiring en `UseCaseConfig`.
- Mappers explícitos en lugar de ModelMapper.
- El frontend usa `fetch` mediante clientes centralizados (`src/api/client.js`, `authApi.js`, `catalogoApi.js`).
- La sesión se guarda como un único objeto `ecommerce.session` con token, usuario y expiración.
- El catálogo deriva la identidad del JWT; se eliminaron los parámetros `usuarioId` del carrito y las compras.
- El checkout descuenta stock de forma atómica, guarda la compra, vacía el carrito y publica la notificación después del commit.
- El carrito ya no reserva stock; solo el checkout lo descuenta.
- Los scripts de arranque usan Docker Compose y rutas relativas.
- El README, la guía de instalación y la guía de Postman reflejan el stack open source.

### Corregido

- El login del frontend usaba URLs hardcodeadas y un contrato distinto al del backend.
- El frontend enviaba `X-User-Role` y `usuarioId`, lo que permitía suplantar usuarios.
- El registro permitía inyectar roles desde el cliente.
- Los errores del backend se perdían en el frontend; ahora se muestran mensajes útiles.
- El contador del carrito se desincronizaba entre componentes.
- El checkout podía dejar stock negativo o compras sin guardar.
- Los servicios no tenían healthchecks reales.
- Las credenciales de AWS/Twilio estaban hardcodeadas en scripts `.bat`.

### Eliminado

- AWS SDK (SQS y SES), configuración de AWS y credenciales en scripts.
- Twilio SDK y configuración de Twilio.
- ModelMapper.
- Axios en el frontend.
- `gh-pages` y el despliegue en GitHub Pages.
- `render.yaml`, `netlify.toml`, `DEPLOY_STEP_BY_STEP.md` y configuraciones de despliegue obsoletas.
- `README_INICIO.md` y `refactor-to-clean.md`, reemplazados por documentación mantenida.
- Logs de build versionados.

### Seguridad

- **Acción obligatoria:** las versiones anteriores del repositorio público contenían una clave de AWS IAM y un token de Twilio. El historial público fue purgado el 2026-11-12 y el repositorio anterior quedó archivado como `ECOMMERCE-archive` (privado). Rota o revoca esas credenciales igualmente; la purga no las invalida.
- Los secretos ya no se versionan. `.env` está ignorado y los `.env.example` solo tienen marcadores.
- `JWT_SECRET` debe tener al menos 32 caracteres y ser idéntico en Auth y Catálogo.
- RabbitMQ usa un usuario distinto de `guest`.
- El catálogo valida propiedad o rol `ADMIN` en usuarios, carrito y compras.
- Las contraseñas usan BCrypt y nunca se devuelven en las respuestas.

### Pruebas

- Auth: 19 tests.
- Catálogo: 20 tests.
- Notificaciones: 16 tests.
- Frontend: 12 tests.
- Build de producción del frontend sin warnings.

### Migración desde 1.x

1. Rota las credenciales de AWS y Twilio.
2. Crea un `.env` desde `.env.example` y define `POSTGRES_PASSWORD`, `RABBITMQ_PASSWORD` y `JWT_SECRET`.
3. Ejecuta `docker compose up -d --build`.
4. Actualiza el rol del primer administrador en PostgreSQL y vuelve a iniciar sesión.
5. El historial público ya fue purgado. Si clonaste antes de la purga, vuelve a clonar el repositorio.
6. Reemplaza cualquier integración externa que esperara `X-User-Role` o `usuarioId` por el JWT.
7. Configura un SMTP real y un gateway SMS si vas a producción.

## [1.0.0] - 2025-12-04

Versión inicial con microservicios Spring Boot, React, PostgreSQL, AWS SQS/SES y Twilio. Esta versión queda reemplazada por 2.0.0.
