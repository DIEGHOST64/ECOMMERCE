# API Documentation

## 🌐 Endpoints de la API

Este documento describe los endpoints disponibles en cada microservicio.

## Auth Service (Puerto 9090)

Base URL: `http://localhost:9090`

### Autenticación

#### Registrar Usuario
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "usuario",
  "email": "usuario@ejemplo.com",
  "password": "password123",
  "nombre": "Nombre Completo",
  "telefono": "+1234567890"
}
```

**Respuesta exitosa (201 Created):**
```json
{
  "id": 1,
  "username": "usuario",
  "email": "usuario@ejemplo.com",
  "nombre": "Nombre Completo",
  "telefono": "+1234567890",
  "rol": "USER"
}
```

#### Iniciar Sesión
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "usuario",
  "password": "password123"
}
```

**Respuesta exitosa (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "id": 1,
    "username": "usuario",
    "email": "usuario@ejemplo.com",
    "rol": "USER"
  }
}
```

#### Obtener Usuario por ID
```http
GET /api/auth/usuarios/{id}
```

**Respuesta exitosa (200 OK):**
```json
{
  "id": 1,
  "username": "usuario",
  "email": "usuario@ejemplo.com",
  "nombre": "Nombre Completo",
  "rol": "USER"
}
```

---

## Catálogo Service (Puerto 9092)

Base URL: `http://localhost:9092`

### Productos

#### Listar Todos los Productos
```http
GET /api/productos
```

**Respuesta exitosa (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Producto 1",
    "descripcion": "Descripción del producto",
    "precio": 99.99,
    "stock": 50,
    "imagenUrl": "https://ejemplo.com/imagen.jpg",
    "categoriaId": 1
  }
]
```

#### Obtener Producto por ID
```http
GET /api/productos/{id}
```

#### Crear Producto (Admin)
```http
POST /api/productos
Content-Type: application/json

{
  "nombre": "Nuevo Producto",
  "descripcion": "Descripción del producto",
  "precio": 149.99,
  "stock": 30,
  "imagenUrl": "https://ejemplo.com/imagen.jpg",
  "categoriaId": 1
}
```

#### Actualizar Producto (Admin)
```http
PUT /api/productos/{id}
Content-Type: application/json

{
  "nombre": "Producto Actualizado",
  "precio": 129.99,
  "stock": 25
}
```

#### Eliminar Producto (Admin)
```http
DELETE /api/productos/{id}
```

### Categorías

#### Listar Categorías
```http
GET /api/categorias
```

**Respuesta exitosa (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Electrónica",
    "descripcion": "Productos electrónicos"
  }
]
```

#### Crear Categoría (Admin)
```http
POST /api/categorias
Content-Type: application/json

{
  "nombre": "Nueva Categoría",
  "descripcion": "Descripción de la categoría"
}
```

### Carrito de Compras

#### Obtener Carrito del Usuario
```http
GET /api/carrito/{usuarioId}
```

**Respuesta exitosa (200 OK):**
```json
{
  "id": 1,
  "usuarioId": 1,
  "items": [
    {
      "id": 1,
      "productoId": 5,
      "producto": {
        "id": 5,
        "nombre": "Producto",
        "precio": 99.99,
        "imagenUrl": "..."
      },
      "cantidad": 2,
      "precioUnitario": 99.99
    }
  ],
  "total": 199.98
}
```

#### Agregar Producto al Carrito
```http
POST /api/carrito/{usuarioId}/items
Content-Type: application/json

{
  "productoId": 5,
  "cantidad": 2
}
```

#### Actualizar Cantidad de Item
```http
PUT /api/carrito/{usuarioId}/items/{itemId}
Content-Type: application/json

{
  "cantidad": 3
}
```

#### Eliminar Item del Carrito
```http
DELETE /api/carrito/{usuarioId}/items/{itemId}
```

#### Vaciar Carrito
```http
DELETE /api/carrito/{usuarioId}
```

### Pedidos

#### Crear Pedido desde Carrito
```http
POST /api/pedidos/{usuarioId}
Content-Type: application/json

{
  "direccionEnvio": "Calle Principal 123, Ciudad",
  "metodoPago": "TARJETA"
}
```

**Respuesta exitosa (201 Created):**
```json
{
  "id": 1,
  "usuarioId": 1,
  "items": [...],
  "total": 199.98,
  "estado": "PENDIENTE",
  "fechaCreacion": "2025-11-26T10:30:00",
  "direccionEnvio": "Calle Principal 123, Ciudad",
  "metodoPago": "TARJETA"
}
```

#### Listar Pedidos del Usuario
```http
GET /api/pedidos/usuario/{usuarioId}
```

#### Obtener Pedido por ID
```http
GET /api/pedidos/{id}
```

#### Actualizar Estado del Pedido (Admin)
```http
PUT /api/pedidos/{id}/estado
Content-Type: application/json

{
  "estado": "ENVIADO"
}
```

**Estados disponibles:**
- `PENDIENTE`
- `CONFIRMADO`
- `ENVIADO`
- `ENTREGADO`
- `CANCELADO`

---

## Notificaciones Service (Puerto 9093)

Base URL: `http://localhost:9093`

### Notificaciones

#### Enviar Email
```http
POST /api/notificaciones/email
Content-Type: application/json

{
  "destinatario": "usuario@ejemplo.com",
  "asunto": "Confirmación de Pedido",
  "mensaje": "Tu pedido #123 ha sido confirmado"
}
```

#### Enviar SMS
```http
POST /api/notificaciones/sms
Content-Type: application/json

{
  "numeroTelefono": "+1234567890",
  "mensaje": "Tu código de verificación es: 123456"
}
```

---

## 🔐 Autenticación

La mayoría de los endpoints requieren autenticación. Incluye el token JWT en el header:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 📝 Códigos de Estado HTTP

- `200 OK`: Solicitud exitosa
- `201 Created`: Recurso creado exitosamente
- `204 No Content`: Solicitud exitosa sin contenido de respuesta
- `400 Bad Request`: Datos inválidos en la solicitud
- `401 Unauthorized`: No autenticado o token inválido
- `403 Forbidden`: No autorizado para realizar la acción
- `404 Not Found`: Recurso no encontrado
- `500 Internal Server Error`: Error del servidor

## 🧪 Probar la API

### Usando cURL

**Registrar usuario:**
```bash
curl -X POST http://localhost:9090/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"test\",\"email\":\"test@test.com\",\"password\":\"123456\"}"
```

**Listar productos:**
```bash
curl http://localhost:9092/api/productos
```

### Usando Postman

1. Importa los endpoints desde este documento
2. Configura las variables de entorno:
   - `AUTH_URL`: http://localhost:9090
   - `CATALOGO_URL`: http://localhost:9092
   - `TOKEN`: (se obtiene del login)

## 🔄 CORS

El backend está configurado para permitir peticiones desde:
- `http://localhost:3000` (Frontend React)

Si necesitas agregar otros orígenes, modifica la configuración CORS en cada microservicio.

---

Para más información sobre el proyecto, consulta el `README.md` principal.
