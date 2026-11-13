# API

Documentación de los endpoints expuestos por los servicios. Todos los cuerpos son JSON y las respuestas de error también.

- Auth: `http://localhost:9090`
- Catálogo: `http://localhost:9092`
- Notificaciones: `http://localhost:9093` (solo healthcheck)

## Autenticación

Los endpoints protegidos requieren:

```http
Authorization: Bearer <token>
```

El token se obtiene en `POST /api/ecommerce/usuario/login`. La identidad (id y rol) se extrae del JWT; el cliente **no** debe enviar `usuarioId` ni `X-User-Role`.

### Roles

| Rol | Permisos |
| --- | --- |
| Público | Registro, login, consultar productos, healthchecks |
| `USER` | Carrito, compras propias, perfil propio |
| `ADMIN` | Todo lo anterior + CRUD de productos + listar todas las compras + eliminar usuarios |

El registro siempre crea usuarios con rol `USER`. Para crear un administrador, promueve el usuario directamente en PostgreSQL (ver [INSTALL.md](./INSTALL.md#crear-un-administrador)).

---

## Auth Service

Base URL: `http://localhost:9090`

### Registrar usuario

```http
POST /api/ecommerce/usuario/save
Content-Type: application/json
```

```json
{
  "nombre": "Ana Pérez",
  "email": "ana@ejemplo.com",
  "password": "secreto123",
  "telefono": "+573001234567",
  "edad": 28
}
```

Validaciones:

- `nombre`: obligatorio, máximo 100 caracteres.
- `email`: obligatorio, formato válido, máximo 120 caracteres.
- `password`: obligatorio, entre 6 y 72 caracteres.
- `telefono`: opcional, máximo 30 caracteres.
- `edad`: opcional, entre 0 y 120.

**201 Created**

```json
{
  "id": 1,
  "nombre": "Ana Pérez",
  "email": "ana@ejemplo.com",
  "telefono": "+573001234567",
  "role": "USER",
  "edad": 28
}
```

### Iniciar sesión

```http
POST /api/ecommerce/usuario/login
Content-Type: application/json
```

```json
{
  "email": "ana@ejemplo.com",
  "password": "secreto123"
}
```

**200 OK**

```json
{
  "id": 1,
  "email": "ana@ejemplo.com",
  "nombre": "Ana Pérez",
  "role": "USER",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiraEnSegundos": 86400,
  "mensaje": "Login exitoso"
}
```

Errores:

- `401` credenciales inválidas.
- `400` email con formato inválido o campos vacíos.

### Obtener usuario

```http
GET /api/ecommerce/usuario/{id}
Authorization: Bearer <token>
```

Permitido para el propio usuario o para un `ADMIN`.

**200 OK**

```json
{
  "id": 1,
  "nombre": "Ana Pérez",
  "email": "ana@ejemplo.com",
  "telefono": "+573001234567",
  "role": "USER",
  "edad": 28
}
```

### Verificar existencia (interno)

```http
GET /api/ecommerce/usuario/{id}/existe
```

Endpoint público pensado para integraciones internas. Devuelve `true` o `false` y no expone datos personales.

### Actualizar usuario

```http
PUT /api/ecommerce/usuario/update
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "id": 1,
  "nombre": "Ana Pérez",
  "email": "ana@ejemplo.com",
  "password": "nuevaClave123",
  "telefono": "+573009876543",
  "edad": 29
}
```

- El `id` es obligatorio.
- Los campos omitidos o nulos no se modifican.
- El rol no se puede cambiar desde este endpoint.
- Permitido para el propio usuario o para un `ADMIN`.

**200 OK** con el usuario actualizado.

### Eliminar usuario

```http
DELETE /api/ecommerce/usuario/{id}
Authorization: Bearer <token>
```

Solo `ADMIN`.

**204 No Content**

---

## Catálogo Service

Base URL: `http://localhost:9092`

### Productos

#### Listar productos (público)

```http
GET /api/ecommerce/producto
```

**200 OK**

```json
[
  {
    "id": 1,
    "nombre": "Laptop Dell XPS 15",
    "descripcion": "Laptop de alto rendimiento",
    "precio": 1299.99,
    "stock": 10,
    "imagenUrl": "https://ejemplo.com/laptop.jpg"
  }
]
```

#### Obtener producto (público)

```http
GET /api/ecommerce/producto/{id}
```

**200 OK** con el producto. `404` si no existe.

#### Crear producto (ADMIN)

```http
POST /api/ecommerce/producto
Authorization: Bearer <token-admin>
Content-Type: application/json
```

```json
{
  "nombre": "Laptop Dell XPS 15",
  "descripcion": "Laptop de alto rendimiento",
  "precio": 1299.99,
  "stock": 10,
  "imagenUrl": "https://ejemplo.com/laptop.jpg"
}
```

Validaciones: nombre obligatorio (máx. 150), descripción máx. 2000, precio mayor a 0 con hasta 2 decimales, stock mayor o igual a 0, `imagenUrl` máx. 2000.

**201 Created** con el producto.

#### Actualizar producto (ADMIN)

```http
PUT /api/ecommerce/producto/{id}
Authorization: Bearer <token-admin>
Content-Type: application/json
```

Mismo cuerpo que la creación. **200 OK** con el producto actualizado.

#### Eliminar producto (ADMIN)

```http
DELETE /api/ecommerce/producto/{id}
Authorization: Bearer <token-admin>
```

**204 No Content**

### Carrito

Todos los endpoints requieren JWT. El carrito pertenece al usuario del token; no se acepta `usuarioId` por parámetro.

#### Ver carrito

```http
GET /api/ecommerce/carrito
Authorization: Bearer <token>
```

**200 OK**

```json
{
  "id": 1,
  "usuarioId": 1,
  "items": [
    {
      "id": 10,
      "productoId": 5,
      "nombreProducto": "Laptop Dell XPS 15",
      "precioUnitario": 1299.99,
      "cantidad": 2,
      "subtotal": 2599.98,
      "imagenUrl": "https://ejemplo.com/laptop.jpg"
    }
  ],
  "precioTotal": 2599.98
}
```

Si el usuario aún no tiene carrito, se devuelve un carrito vacío.

#### Agregar producto

```http
POST /api/ecommerce/carrito/agregar
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "productoId": 5,
  "cantidad": 2
}
```

- `cantidad`: entre 1 y 10 por operación.
- No descuenta stock; solo valida que el producto exista.
- Si el producto ya está en el carrito, se suma la cantidad.

**200 OK** con el carrito actualizado.

#### Actualizar cantidad

```http
PUT /api/ecommerce/carrito/actualizar/{productoId}?cantidad=3
Authorization: Bearer <token>
```

`cantidad` es un query param obligatorio, entre 1 y 10.

**200 OK** con el carrito actualizado.

#### Eliminar producto

```http
DELETE /api/ecommerce/carrito/eliminar/{productoId}
Authorization: Bearer <token>
```

**200 OK** con el carrito actualizado.

#### Vaciar carrito

```http
DELETE /api/ecommerce/carrito/vaciar
Authorization: Bearer <token>
```

**204 No Content**

### Compras

#### Realizar compra

```http
POST /api/ecommerce/compras/realizar
Authorization: Bearer <token>
```

Convierte el carrito actual en una compra:

1. Valida el usuario contra Auth.
2. Verifica que el carrito no esté vacío.
3. Descuenta el stock de cada producto de forma atómica.
4. Guarda la compra con estado `COMPLETADA`.
5. Vacía el carrito.
6. Publica la notificación después del commit.

**201 Created**

```json
{
  "id": 12,
  "usuarioId": 1,
  "fechaCompra": "2026-11-12T10:30:00",
  "items": [
    {
      "id": 30,
      "productoId": 5,
      "nombreProducto": "Laptop Dell XPS 15",
      "precioUnitario": 1299.99,
      "cantidad": 2,
      "subtotal": 2599.98,
      "imagenUrl": "https://ejemplo.com/laptop.jpg"
    }
  ],
  "total": 2599.98,
  "estado": "COMPLETADA"
}
```

Errores:

- `400` carrito vacío.
- `409` stock insuficiente.
- `404` usuario no encontrado.
- `503` Auth no disponible.

#### Listar compras de un usuario

```http
GET /api/ecommerce/compras/usuario/{usuarioId}
Authorization: Bearer <token>
```

Permitido para el propio usuario o un `ADMIN`.

**200 OK** con la lista de compras.

#### Obtener compra

```http
GET /api/ecommerce/compras/{compraId}
Authorization: Bearer <token>
```

Permitido para el dueño de la compra o un `ADMIN`.

#### Listar todas las compras (ADMIN)

```http
GET /api/ecommerce/compras
Authorization: Bearer <token-admin>
```

**200 OK** con todas las compras.

---

## Notificaciones Service

Base URL: `http://localhost:9093`

No expone endpoints de negocio. Los mensajes llegan por RabbitMQ.

### Healthcheck

```http
GET /actuator/health
```

**200 OK**

```json
{ "status": "UP" }
```

También disponible en Auth (`:9090`) y Catálogo (`:9092`).

---

## Formato de errores

Los errores de negocio y validación usan:

```json
{
  "timestamp": "2026-11-12T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Datos invalidos",
  "path": "/api/ecommerce/usuario/save",
  "fieldErrors": {
    "email": "El email no es valido",
    "password": "La contrasena es obligatoria"
  }
}
```

`fieldErrors` solo aparece en errores de validación. Los errores de seguridad (`401`/`403`) devuelven `timestamp`, `status`, `error` y `message`.

| Código | Significado |
| --- | --- |
| 200 | Operación exitosa |
| 201 | Recurso creado |
| 204 | Operación exitosa sin cuerpo |
| 400 | Datos inválidos |
| 401 | Token ausente, inválido o expirado |
| 403 | Sin permisos para el recurso |
| 404 | Recurso no encontrado |
| 409 | Conflicto (stock, email duplicado) |
| 503 | Servicio dependiente no disponible |

---

## Ejemplos con cURL

### Registrar y hacer login

```bash
curl -X POST http://localhost:9090/api/ecommerce/usuario/save \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ana","email":"ana@ejemplo.com","password":"secreto123","telefono":"+573001234567","edad":28}'

curl -X POST http://localhost:9090/api/ecommerce/usuario/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ana@ejemplo.com","password":"secreto123"}'
```

### Listar productos (público)

```bash
curl http://localhost:9092/api/ecommerce/producto
```

### Agregar al carrito y comprar

```bash
TOKEN="<token-del-login>"

curl -X POST http://localhost:9092/api/ecommerce/carrito/agregar \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"productoId":1,"cantidad":2}'

curl -X POST http://localhost:9092/api/ecommerce/compras/realizar \
  -H "Authorization: Bearer $TOKEN"
```

### Crear un producto como administrador

```bash
ADMIN_TOKEN="<token-de-un-admin>"

curl -X POST http://localhost:9092/api/ecommerce/producto \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Teclado","descripcion":"Mecanico","precio":89.99,"stock":20,"imagenUrl":null}'
```

---

## CORS

Los orígenes permitidos se configuran con `CORS_ALLOWED_ORIGINS` en Auth y Catálogo. En local, el valor por defecto es `http://localhost:3000,http://localhost:5173`.

Si el frontend se sirve desde otro dominio, actualiza esa variable y reconstruye el frontend con las URLs públicas correctas.

Para una guía paso a paso con Postman consulta [POSTMAN_GUIDE.md](./POSTMAN_GUIDE.md).
