# 🏗️ Arquitectura del Sistema E-Commerce

## Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────────────┐
│                          FRONTEND (React)                        │
│                      http://localhost:3000                       │
│                                                                  │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐          │
│  │  Login/  │ │ Product  │ │  Cart    │ │  Orders  │          │
│  │ Register │ │   List   │ │          │ │          │          │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘          │
│                                                                  │
│                    ▼ HTTP/REST (Axios)                          │
└─────────────────────────────────────────────────────────────────┘
                              │
                ┌─────────────┴──────────────┐
                │                            │
                ▼                            ▼
┌──────────────────────────┐   ┌──────────────────────────┐
│   AUTH MICROSERVICE      │   │  CATALOGO MICROSERVICE   │
│   Port: 9090             │   │   Port: 9092             │
├──────────────────────────┤   ├──────────────────────────┤
│                          │   │                          │
│  ┌────────────────────┐  │   │  ┌────────────────────┐  │
│  │  Controllers       │  │   │  │  Controllers       │  │
│  │  - AuthController  │  │   │  │  - ProductCtrl     │  │
│  │  - UserController  │  │   │  │  - CategoryCtrl    │  │
│  └────────────────────┘  │   │  │  - CartCtrl        │  │
│           │              │   │  │  - OrderCtrl       │  │
│           ▼              │   │  └────────────────────┘  │
│  ┌────────────────────┐  │   │           │              │
│  │  Application       │  │   │           ▼              │
│  │  (Use Cases)       │  │   │  ┌────────────────────┐  │
│  │  - RegisterUser    │  │   │  │  Application       │  │
│  │  - LoginUser       │  │   │  │  (Use Cases)       │  │
│  │  - ValidateToken   │  │   │  │  - ManageProducts  │  │
│  └────────────────────┘  │   │  │  - ManageCart      │  │
│           │              │   │  │  - CreateOrder     │  │
│           ▼              │   │  └────────────────────┘  │
│  ┌────────────────────┐  │   │           │              │
│  │  Domain            │  │   │           ▼              │
│  │  (Entities)        │  │   │  ┌────────────────────┐  │
│  │  - Usuario         │  │   │  │  Domain            │  │
│  │  - Rol             │  │   │  │  (Entities)        │  │
│  └────────────────────┘  │   │  │  - Producto        │  │
│           │              │   │  │  - Categoria       │  │
│           ▼              │   │  │  - Carrito         │  │
│  ┌────────────────────┐  │   │  │  - Pedido          │  │
│  │  Infrastructure    │  │   │  └────────────────────┘  │
│  │  - Repositories    │  │   │           │              │
│  │  - Security        │  │   │           ▼              │
│  │  - Config          │  │   │  ┌────────────────────┐  │
│  └────────────────────┘  │   │  │  Infrastructure    │  │
│           │              │   │  │  - Repositories    │  │
└───────────┼──────────────┘   │  │  - SQS Producer    │◄─┐
            │                  │  │  - Config          │  │
            ▼                  │  └────────────────────┘  │
   ┌────────────────┐          │           │              │
   │   PostgreSQL   │          └───────────┼──────────────┘
   │  ecommerce_auth│                      │
   │   Port: 5432   │                      ▼
   └────────────────┘             ┌────────────────┐
                                  │   PostgreSQL   │
                                  │   ecommerce    │
                                  │   Port: 5432   │
                                  └────────────────┘
                                           
                                           
            ┌──────── AWS SQS Queue ◄──────┘
            │      (Async Messages)
            │
            ▼
┌──────────────────────────────────┐
│  NOTIFICACIONES MICROSERVICE     │
│       Port: 9093                 │
├──────────────────────────────────┤
│                                  │
│  ┌────────────────────────────┐  │
│  │  SQS Consumer              │  │
│  │  (Polling Messages)        │  │
│  └────────────────────────────┘  │
│              │                   │
│              ▼                   │
│  ┌────────────────────────────┐  │
│  │  Notification Service      │  │
│  │  - Email Handler (AWS SES) │  │───► AWS SES
│  │  - SMS Handler (Twilio)    │  │───► Twilio API
│  └────────────────────────────┘  │
│                                  │
└──────────────────────────────────┘
```

## 📦 Componentes del Sistema

### 1. Frontend (React - Puerto 3000)

**Responsabilidades:**
- Interfaz de usuario
- Gestión de estado local
- Comunicación con APIs backend
- Navegación entre páginas

**Tecnologías:**
- React 18
- React Router DOM
- Axios (HTTP client)
- React Icons

**Componentes Principales:**
- `Login/Register`: Autenticación de usuarios
- `ProductList`: Catálogo de productos
- `Cart`: Carrito de compras
- `Orders`: Historial de pedidos
- `AdminPanel`: Panel de administración

### 2. Auth Microservice (Puerto 9090)

**Responsabilidades:**
- Registro y autenticación de usuarios
- Gestión de sesiones
- Autorización y roles
- Validación de tokens JWT

**Tecnologías:**
- Spring Boot 3.5.5
- Spring Security
- Spring Data JPA
- PostgreSQL

**Arquitectura:**
- **Controllers**: Manejo de peticiones HTTP
- **Application**: Casos de uso (lógica de negocio)
- **Domain**: Entidades y reglas de negocio
- **Infrastructure**: Repositorios, configuración, seguridad

**Base de Datos:** `ecommerce_auth`

### 3. Catálogo Microservice (Puerto 9092)

**Responsabilidades:**
- Gestión de productos y categorías
- Carrito de compras
- Creación y seguimiento de pedidos
- Envío de notificaciones asíncronas (SQS)

**Tecnologías:**
- Spring Boot 3.5.7
- Spring Data JPA
- PostgreSQL
- AWS SDK (SQS)

**Arquitectura:**
- **Controllers**: Endpoints REST
- **Application**: Lógica de negocio
- **Domain**: Modelos de datos
- **Infrastructure**: Persistencia, mensajería

**Base de Datos:** `ecommerce`

### 4. Notificaciones Microservice (Puerto 9093)

**Responsabilidades:**
- Consumir mensajes de SQS
- Enviar correos electrónicos (AWS SES)
- Enviar SMS (Twilio)
- Procesamiento asíncrono de notificaciones

**Tecnologías:**
- Spring Boot 3.5.5
- AWS SDK (SQS, SES)
- Twilio API

**Patrón:**
- Consumer asíncrono (polling de SQS)
- Sin base de datos propia

## 🔄 Flujo de Datos

### Registro de Usuario

```
1. Usuario completa formulario de registro en Frontend
2. Frontend → POST /api/auth/register → Auth Service
3. Auth Service valida datos
4. Auth Service guarda usuario en BD (ecommerce_auth)
5. Auth Service → Response (usuario creado) → Frontend
6. Frontend redirige a login
```

### Compra de Producto

```
1. Usuario selecciona producto → Frontend
2. Frontend → POST /api/carrito/{userId}/items → Catálogo Service
3. Catálogo Service agrega item al carrito (BD: ecommerce)
4. Usuario confirma compra
5. Frontend → POST /api/pedidos/{userId} → Catálogo Service
6. Catálogo Service:
   a. Crea pedido en BD
   b. Reduce stock de productos
   c. Vacía el carrito
   d. Envía mensaje a SQS (confirmación de pedido)
7. Notificaciones Service:
   a. Recibe mensaje de SQS
   b. Envía email de confirmación (AWS SES)
   c. Envía SMS de confirmación (Twilio)
8. Frontend muestra confirmación al usuario
```

## 🗄️ Base de Datos

### ecommerce_auth (Auth Service)

**Tablas:**
- `usuarios`: id, username, email, password_hash, nombre, telefono, rol, fecha_creacion
- `roles`: id, nombre (USER, ADMIN)

### ecommerce (Catálogo Service)

**Tablas:**
- `productos`: id, nombre, descripcion, precio, stock, imagen_url, categoria_id
- `categorias`: id, nombre, descripcion
- `carritos`: id, usuario_id, fecha_creacion
- `carrito_items`: id, carrito_id, producto_id, cantidad, precio_unitario
- `pedidos`: id, usuario_id, total, estado, fecha_creacion, direccion_envio, metodo_pago
- `pedido_items`: id, pedido_id, producto_id, cantidad, precio_unitario

## 🔐 Seguridad

### Autenticación
- JWT (JSON Web Tokens) para sesiones
- Tokens generados por Auth Service
- Validación en cada microservicio

### Autorización
- Roles: `USER`, `ADMIN`
- Endpoints protegidos según rol
- CORS configurado para permitir Frontend

### Seguridad de Datos
- Contraseñas hasheadas (BCrypt)
- Variables de entorno para credenciales (.env)
- HTTPS en producción (recomendado)

## 📨 Comunicación entre Servicios

### Síncrona (REST)
- Frontend ↔ Auth Service
- Frontend ↔ Catálogo Service

### Asíncrona (Mensajería)
- Catálogo Service → SQS → Notificaciones Service

**Ventajas del patrón asíncrono:**
- Desacoplamiento de servicios
- Resiliencia ante fallos
- Escalabilidad independiente
- No bloquea la respuesta al usuario

## 🚀 Escalabilidad

### Horizontal Scaling
Cada microservicio puede escalar independientemente:
- **Auth**: Añadir instancias si hay muchos registros/logins
- **Catálogo**: Escalar para manejar más pedidos
- **Notificaciones**: Múltiples consumers de SQS

### Load Balancing (Producción)
```
         ┌──────────┐
         │  Nginx   │ ← Load Balancer
         └────┬─────┘
              │
    ┌─────────┼──────────┐
    ▼         ▼          ▼
  Auth-1   Auth-2    Auth-3
```

## 🔧 Patrones de Diseño Utilizados

1. **Clean Architecture**: Separación de capas (Domain, Application, Infrastructure)
2. **Repository Pattern**: Abstracción de acceso a datos
3. **DTO Pattern**: Transferencia de datos entre capas
4. **Publisher-Subscriber**: Mensajería asíncrona con SQS
5. **Service Layer**: Lógica de negocio encapsulada
6. **Dependency Injection**: Inyección de dependencias con Spring

## 📊 Tecnologías del Stack

| Componente | Tecnología | Versión |
|-----------|-----------|---------|
| Frontend | React | 18.2.0 |
| Backend | Spring Boot | 3.5.x |
| Lenguaje | Java | 17 |
| Base de Datos | PostgreSQL | 12+ |
| Mensajería | AWS SQS | SDK 2.20.26 |
| Email | AWS SES | SDK 2.20.26 |
| SMS | Twilio | 9.14.1 |
| Build Tool | Maven | 3.6+ |
| Package Manager | npm | 8+ |

## 🎯 Próximos Pasos / Roadmap

- [ ] Implementar API Gateway (Spring Cloud Gateway)
- [ ] Agregar Service Discovery (Eureka)
- [ ] Implementar Circuit Breaker (Resilience4j)
- [ ] Agregar monitoreo (Prometheus + Grafana)
- [ ] Implementar logs centralizados (ELK Stack)
- [ ] Containerización (Docker + Kubernetes)
- [ ] CI/CD Pipeline (GitHub Actions)
- [ ] Tests de integración
- [ ] Documentación Swagger/OpenAPI

---

Para más detalles técnicos, consulta `README.md`, `API.md` y `DEPENDENCIES.md`.
