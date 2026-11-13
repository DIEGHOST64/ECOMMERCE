# E-Commerce Microservices Platform

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green?style=for-the-badge&logo=springboot)
![React](https://img.shields.io/badge/React-18-blue?style=for-the-badge&logo=react)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.13-orange?style=for-the-badge&logo=rabbitmq)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?style=for-the-badge&logo=docker)

Plataforma de comercio electrónico con microservicios Spring Boot, frontend React y un stack **100 % open source**. Ya no depende de AWS ni de Twilio.

## Reemplazos respecto a la versión anterior

| Antes | Ahora |
| --- | --- |
| AWS SQS | RabbitMQ (exchange topic + cola con reintentos y DLQ) |
| AWS SES | SMTP: Mailpit en desarrollo, cualquier servidor SMTP en producción |
| Twilio SMS | Adaptador SMS por webhook HTTP o modo `log` |
| Rol enviado en `X-User-Role` | JWT firmado y validado con Spring Security |
| `usuarioId` en la URL/query | Identidad derivada del JWT |
| ModelMapper | Mappers explícitos por caso de uso |
| Secretos en scripts `.bat` | Variables de entorno en `.env` (ignorado por Git) |

## Inicio rápido con Docker

Requisitos: Docker Desktop con Docker Compose v2.

```bash
git clone https://github.com/DIEGHOST64/ECOMMERCE.git
cd ECOMMERCE

cp .env.example .env
# Windows: copy .env.example .env
```

Edita `.env` y cambia como mínimo:

- `POSTGRES_PASSWORD`
- `RABBITMQ_PASSWORD`
- `JWT_SECRET` (mínimo 32 caracteres; genera uno con `openssl rand -base64 48`)

```bash
docker compose up -d --build
docker compose ps
```

En Windows también puedes usar:

```powershell
.\setup.bat
# edita .env
powershell -ExecutionPolicy Bypass -File .\start-services.ps1
```

Servicios disponibles:

| Servicio | URL |
| --- | --- |
| Frontend | http://localhost:3000 |
| Auth API | http://localhost:9090 |
| Catálogo API | http://localhost:9092 |
| Mailpit (correo) | http://localhost:8025 |
| RabbitMQ Management | http://localhost:15672 |

Para crear el primer administrador, registra un usuario desde el frontend y promuévelo:

```bash
docker compose exec postgres psql -U postgres -d ecommerce \
  -c "UPDATE usuario SET role = 'ADMIN' WHERE email = 'tu@email.com';"
```

Cierra sesión y vuelve a entrar para que el nuevo JWT incluya el rol.

Detener la plataforma:

```bash
docker compose down          # conserva los datos
docker compose down --volumes  # elimina PostgreSQL y RabbitMQ
```

## Arquitectura

```text
Navegador
   |
   v
Frontend React (nginx)
   |------------------------------+
   v                              v
Auth API :9090                 Catálogo API :9092
   |      \                       |       \
   |       \--> RabbitMQ <--------+        \--> Auth (validar usuario)
   |              |
   v              v
PostgreSQL   Notificaciones :9093
                 |        \
                 v         v
              SMTP      SMS webhook/log
```

- **Auth** registra usuarios, autentica y emite JWT.
- **Catálogo** administra productos, carrito y compras. Valida el JWT localmente y consulta a Auth cuando necesita datos del usuario.
- **Notificaciones** consume RabbitMQ y envía email/SMS. Los fallos se reintentan y, agotados los intentos, van a una DLQ.
- **Frontend** usa clientes API centralizados, guarda la sesión JWT y normaliza roles.

Más detalles en [ARCHITECTURE.md](./ARCHITECTURE.md).

## Clean Architecture

Cada servicio respeta la regla de dependencia: `domain` no conoce frameworks; `application` implementa los casos de uso; `infraestructura` contiene los adaptadores.

```text
servicio/src/main/java/com/ecommerce/<servicio>/
├── domain/
│   ├── model/          Entidades y objetos de valor
│   ├── port/in/        Casos de uso (interfaces)
│   ├── port/out/       Puertos hacia infraestructura
│   └── exception/      Excepciones de negocio
├── application/
│   └── usecase/        Implementación de casos de uso
└── infraestructura/
    ├── config/         Spring, seguridad, RabbitMQ
    ├── entry_points/   Controllers REST + DTOs
    ├── driver_adapters/ JPA, SMTP, SMS, clientes HTTP
    ├── messaging/      Productores/consumidores RabbitMQ
    ├── security/       JWT
    └── mapper/         Traducción dominio <-> persistencia/DTO
```

## Desarrollo local sin contenedores para los servicios

Levanta solo la infraestructura:

```bash
docker compose up -d postgres rabbitmq mailpit
```

Ejecuta un servicio con Maven y las variables del `.env` raíz:

```powershell
.\run-local.ps1 auth
.\run-local.ps1 catalogo
.\run-local.ps1 notificaciones
```

En otra terminal:

```bash
cd frontend
npm ci
npm start
```

El frontend usa por defecto `http://localhost:9090` y `http://localhost:9092`. Puedes cambiarlo con:

```bash
REACT_APP_AUTH_URL=http://localhost:9090
REACT_APP_CATALOGO_URL=http://localhost:9092
```

## Pruebas

```bash
# Backend
cd auth/auth/auth && ./mvnw clean verify
cd catalogo/catalogo && ./mvnw clean verify
cd notificaciones && ./mvnw clean verify

# Frontend
cd frontend
npm test -- --watchAll=false
npm run build
```

En Windows usa `mvnw.cmd` y `npm.cmd`. El workflow de GitHub Actions (`.github/workflows/ci.yml`) ejecuta las tres suites, las pruebas del frontend y valida `docker-compose.yml`.

## Documentación

- [ARCHITECTURE.md](./ARCHITECTURE.md) — capas, flujos y mensajería.
- [API.md](./API.md) — endpoints, roles y ejemplos.
- [POSTMAN_GUIDE.md](./POSTMAN_GUIDE.md) — uso de la colección Postman.
- [DEPLOYMENT.md](./DEPLOYMENT.md) — despliegue con Docker y consideraciones de producción.
- [INSTALL.md](./INSTALL.md) — instalación detallada.
- [DEPENDENCIES.md](./DEPENDENCIES.md) — dependencias y versiones.

## Seguridad

- Contraseñas con BCrypt; JWT firmado con HMAC-SHA256 y expiración configurable.
- Roles `USER` y `ADMIN`; el backend fuerza `USER` en el registro.
- El catálogo deriva el usuario del JWT: no acepta `usuarioId` ni `X-User-Role` del cliente.
- Propiedad verificada en carrito, compras y datos de usuario.
- El stock se descuenta de forma atómica solo al confirmar la compra; el carrito no reserva inventario.
- Los secretos viven en `.env`, que está ignorado por Git.

> **Importante:** versiones anteriores de este repositorio público incluyeron credenciales de AWS y Twilio en el historial de Git. Si clonaste o usaste esas versiones, considera esas credenciales comprometidas: rótalas o revócalas. Eliminar los secretos del `HEAD` no los borra del historial; para eso usa `git filter-repo` o BFG y luego fuerza el push.

## Licencia

MIT. Consulta [LICENSE](./LICENSE).
