# Arquitectura

Documento técnico de la plataforma. Para el inicio rápido consulta [README.md](./README.md); para los endpoints, [API.md](./API.md); para el despliegue, [DEPLOYMENT.md](./DEPLOYMENT.md).

## 1. Vista general

La plataforma es un conjunto de microservicios Spring Boot con un frontend React. Todo el stack es open source y se ejecuta con Docker Compose.

| Componente | Tecnología | Puerto |
| --- | --- | --- |
| Frontend | React 18 + nginx | 3000 |
| Auth API | Spring Boot 3.5 / Java 17 | 9090 |
| Catálogo API | Spring Boot 3.5 / Java 17 | 9092 |
| Notificaciones | Spring Boot 3.5 / Java 17 | 9093 |
| PostgreSQL | PostgreSQL 16 | 5432 |
| RabbitMQ | RabbitMQ 3.13 (management) | 5672 / 15672 |
| SMTP de desarrollo | Mailpit | 1025 / 8025 |

```text
                         Navegador
                             |
                             v
                 Frontend React (nginx :3000)
                    |                    |
          REST + JWT |                    | REST + JWT
                    v                    v
             Auth API :9090       Catálogo API :9092
                    |   \                |   \
                    |    \               |    \--> Auth API (valida usuario,
                    |     \              |          reenviando el JWT)
                    |      \             |
                    v       v            v
              PostgreSQL   RabbitMQ   PostgreSQL
             (usuario)   exchange    (products, carrito,
                          |           item_carrito, compras)
                          v
                 Notificaciones :9093
                    |            |
                    v            v
              SMTP (Mailpit)   SMS webhook / log
```

## 2. Clean Architecture

Cada servicio backend aplica la regla de dependencia: las capas externas conocen a las internas, nunca al contrario.

```text
servicio/src/main/java/com/ecommerce/<servicio>/
├── domain/
│   ├── model/          Entidades y objetos de valor
│   ├── port/in/        Casos de uso (interfaces)
│   ├── port/out/       Puertos hacia infraestructura
│   └── exception/      Excepciones de negocio
├── application/
│   └── usecase/        Implementación de los casos de uso
└── infraestructura/
    ├── config/         Spring, seguridad, RabbitMQ, wiring
    ├── entry_points/   Controllers REST + DTOs + mappers
    ├── driver_adapters/JPA, SMTP, SMS, clientes HTTP
    ├── messaging/      Productores/consumidores RabbitMQ
    ├── security/       Filtro y servicio JWT
    └── mapper/         Traducción dominio <-> persistencia
```

Reglas:

- `domain` no importa Spring, JPA, Jackson ni RabbitMQ. Solo Java y sus propios tipos.
- `application` implementa los puertos de entrada y depende de los puertos de salida. No conoce controllers, JPA ni SMTP.
- `infraestructura` implementa los puertos de salida y adapta las peticiones HTTP a los casos de uso.
- El wiring se concentra en `infraestructura/config` (`UseCaseConfig`), de modo que los casos de uso se construyen con `new` y no con anotaciones de Spring.
- La capa `application` usa `@Transactional` para delimitar transacciones de persistencia; es la única dependencia de framework en esa capa y no filtra tipos de Spring al dominio.

## 3. Servicios

### 3.1 Auth (`auth/auth/auth`)

Responsabilidades:

- Registrar usuarios con contraseña hasheada (BCrypt).
- Autenticar por email y contraseña.
- Emitir JWT firmados con HMAC-SHA256.
- Gestionar el perfil del usuario autenticado.
- Publicar notificaciones de bienvenida/registro en RabbitMQ.

Reglas:

- El rol siempre se fuerza a `USER` en el registro. El rol `ADMIN` se asigna por base de datos.
- El login devuelve los campos planos que consume el frontend (`id`, `email`, `nombre`, `role`) más `token`, `tokenType`, `expiraEnSegundos` y `mensaje`.
- El JWT incluye `sub` (id), `email`, `nombre`, `role`, `iat` y `exp`.
- `JWT_SECRET` debe tener al menos 32 caracteres; el servicio falla al arrancar si no se cumple.

### 3.2 Catálogo (`catalogo/catalogo`)

Responsabilidades:

- CRUD de productos.
- Carrito por usuario.
- Checkout y historial de compras.
- Publicación de la confirmación de compra en RabbitMQ.

Reglas:

- Las lecturas de productos (`GET /api/ecommerce/producto/**`) son públicas.
- Las escrituras de productos requieren rol `ADMIN`.
- El carrito y las compras requieren JWT. El `usuarioId` se obtiene del token, nunca de la URL o del query string.
- El carrito **no reserva stock**. El stock se valida y descuenta de forma atómica en el checkout.
- Para validar los datos del usuario, el servicio llama a Auth reenviando el JWT del solicitante.

### 3.3 Notificaciones (`notificaciones`)

Responsabilidades:

- Consumir mensajes de RabbitMQ.
- Enviar correos por SMTP.
- Enviar SMS mediante un webhook HTTP configurable o escribir el SMS en el log.
- Reintentar fallos y derivar los mensajes agotados a una DLQ.

No expone endpoints de negocio; solo `GET /actuator/health` para el healthcheck. No tiene base de datos propia.

### 3.4 Frontend (`frontend`)

Responsabilidades:

- Interfaz de login, registro, productos, carrito, compras y panel de administración.
- Guardar la sesión JWT en `localStorage` bajo `ecommerce.session`.
- Adjuntar `Authorization: Bearer <token>` en cada llamada protegida.
- Detectar expiración o rechazo del token y cerrar la sesión.
- Normalizar los roles del backend (`ADMIN`, `ROLE_ADMIN`, `CLIENTE`, `USER`) a `ADMIN` o `USER`.

Los clientes HTTP viven en `src/api/` y la sesión en `src/session.js`. No hay URLs ni roles hardcodeados en los componentes.

## 4. Flujos

### 4.1 Registro

```text
Frontend -> POST /api/ecommerce/usuario/save -> Auth
Auth valida, hashea la contraseña y guarda el usuario
Auth publica notificación de bienvenida en RabbitMQ
Frontend redirige al login
```

### 4.2 Login y autorización

```text
Frontend -> POST /api/ecommerce/usuario/login -> Auth
Auth verifica BCrypt y emite un JWT
Frontend guarda token + usuario + expiración
Cada petición protegida envía Authorization: Bearer <token>
Auth y Catálogo validan la firma y la expiración con el mismo JWT_SECRET
```

### 4.3 Carrito

```text
Frontend -> POST /api/ecommerce/carrito/agregar -> Catálogo
Catálogo toma el usuario del JWT y agrega el ítem
El carrito guarda precio unitario, cantidad, subtotal e imagen
No se modifica el stock
```

### 4.4 Checkout

```text
Frontend -> POST /api/ecommerce/compras/realizar -> Catálogo
1. Catálogo valida el usuario contra Auth
2. Lee el carrito del usuario
3. Descuenta el stock de cada producto de forma atómica
   (si un producto no tiene stock, toda la transacción revierte)
4. Guarda la compra con el estado COMPLETADA
5. Elimina el carrito
6. Tras el commit, publica la notificación en RabbitMQ
7. Devuelve la compra al frontend
```

La notificación se publica **después del commit** para no avisar de una compra que finalmente no se guardó. Si RabbitMQ falla, la compra sigue siendo válida y solo se registra una advertencia en el log.

### 4.5 Notificación de compra

```text
Catálogo -> exchange notificaciones.exchange
          -> routing key notificacion.ambos
          -> cola notificaciones.queue
          -> Notificaciones
             ├── SMTP -> Mailpit (desarrollo) o servidor SMTP real
             └── SMS  -> webhook HTTP o log
```

## 5. Mensajería

Topología declarada por Notificaciones:

| Elemento | Nombre por defecto |
| --- | --- |
| Exchange | `notificaciones.exchange` (topic, durable) |
| Cola | `notificaciones.queue` (durable) |
| Routing key email | `notificacion.email` |
| Routing key SMS | `notificacion.sms` |
| Routing key ambos | `notificacion.ambos` |
| Dead letter exchange | `notificaciones.dlx` (direct) |
| Dead letter queue | `notificaciones.dlq` |

Contrato JSON:

```json
{
  "tipo": "AMBOS",
  "email": {
    "destinatario": "usuario@ejemplo.com",
    "asunto": "Confirmacion de Compra #12",
    "cuerpo": "<h1>Compra confirmada</h1>",
    "html": true
  },
  "sms": {
    "numeroTelefono": "+573001234567",
    "mensaje": "Tu compra #12 ha sido confirmada."
  }
}
```

- `tipo` es `EMAIL`, `SMS` o `AMBOS`.
- El productor solo declara el exchange; la cola, el DLX y la DLQ pertenecen a Notificaciones.
- Reintentos: 3 intentos con backoff exponencial (1 s, 2 s, 4 s; máximo 10 s).
- Si se agotan los reintentos, el mensaje va a `notificaciones.dlq`.
- El consumidor ignora el header `__TypeId__` de otros servicios y usa el tipo inferido del listener, por lo que los servicios no comparten clases Java.

## 6. Persistencia

Se usa una única base de datos PostgreSQL (`ecommerce`) compartida por Auth y Catálogo. Cada servicio es dueño de sus tablas y no accede a las del otro.

| Tabla | Servicio | Contenido |
| --- | --- | --- |
| `usuario` | Auth | id, nombre, email único, password BCrypt, teléfono, rol, edad |
| `products` | Catálogo | id, nombre, descripción, precio, stock, imagenUrl |
| `carrito` | Catálogo | id, usuario_id único, precio_total |
| `item_carrito` | Catálogo | líneas de carrito (`id_carrito`) y de compra (`compra_id`) |
| `compras` | Catálogo | id, usuario_id, fecha_compra, total, estado |

`spring.jpa.hibernate.ddl-auto` es configurable (`JPA_DDL_AUTO`, por defecto `update`). Para producción se recomienda usar migraciones (Flyway o Liquibase) y `validate`.

## 7. Seguridad

- Contraseñas con BCrypt; nunca se devuelven en las respuestas.
- JWT firmado con HMAC-SHA256; expiración configurable (`JWT_EXPIRATION_MINUTES`, 24 h por defecto).
- Auth y Catálogo comparten `JWT_SECRET`. Si los secretos no coinciden, Catálogo rechaza todos los tokens.
- Roles `USER` y `ADMIN`; las anotaciones `@PreAuthorize` protegen las operaciones de administración.
- El catálogo no acepta `X-User-Role` ni `usuarioId` del cliente: la identidad sale del JWT.
- Las operaciones sobre datos personales y compras verifican propietario o rol `ADMIN`.
- CORS restringido por `CORS_ALLOWED_ORIGINS`.
- Los secretos viven en `.env` (ignorado por Git). Los `.env.example` solo contienen marcadores.
- RabbitMQ usa un usuario distinto de `guest`; `guest` solo puede conectarse desde loopback.

## 8. Pruebas

| Suite | Cantidad |
| --- | --- |
| Auth | 19 |
| Catálogo | 20 |
| Notificaciones | 16 |
| Frontend | 12 |

Cobertura funcional:

- Auth: casos de uso, login/registro reales con H2, seguridad y validación.
- Catálogo: casos de uso de producto, carrito y compra; integración REST con H2 y JWT.
- Notificaciones: caso de uso, adaptador SMTP, adaptador SMS y listener RabbitMQ.
- Frontend: sesión JWT, normalización de roles y cliente HTTP (errores, timeout, 401).

Comandos en [README.md](./README.md#pruebas) y en el workflow `.github/workflows/ci.yml`.

## 9. Decisiones y límites

- **Docker Compose** es el runtime soportado. No se mantienen configuraciones de Netlify/Render/GitHub Pages.
- **RabbitMQ** sustituye a SQS. El modo de entrega es al menos una vez; los consumidores deben ser idempotentes.
- **SMTP** sustituye a SES. Mailpit permite inspeccionar los correos en desarrollo.
- **SMS por webhook/log** sustituye a Twilio. Cualquier gateway HTTP (Jasmin, Kannel, un gateway Android) puede implementar el contrato.
- **Base de datos compartida**: simplifica el despliegue local, pero acopla el esquema. Si los servicios evolucionan por separado, conviene separar bases o adoptar migraciones por servicio.
- **Sin API Gateway ni service discovery**: el frontend conoce las dos URLs y Catálogo conoce la URL de Auth por configuración.
- **Sin reserva de stock en el carrito**: evita bloqueos de inventario; el checkout es la única operación que descuenta.
- **JWT sin refresh token**: al expirar, el usuario vuelve a iniciar sesión.

## 10. Roadmap

- Migraciones de esquema con Flyway/Liquibase.
- Refresh tokens y revocación.
- API Gateway y rate limiting.
- Circuit breaker entre Catálogo y Auth.
- Métricas con Prometheus/Grafana y trazas con OpenTelemetry.
- Idempotencia explícita en el consumidor de notificaciones.
- Tests end-to-end del flujo completo con Docker Compose.
- Documentación OpenAPI generada desde los controllers.
