# Guía de Postman

La colección `POSTMAN_COLLECTION.json` usa autenticación Bearer a nivel de colección. Al hacer login, el token y el id del usuario se guardan automáticamente en variables.

## 1. Importar

1. Abre Postman.
2. **Import** → selecciona `POSTMAN_COLLECTION.json`.
3. La colección **E-Commerce API** aparecerá con las carpetas Auth, Productos, Carrito, Compras y Health.

## 2. Variables

| Variable | Valor inicial | Se actualiza |
| --- | --- | --- |
| `auth_url` | `http://localhost:9090` | Manual |
| `catalogo_url` | `http://localhost:9092` | Manual |
| `token` | vacío | Al ejecutar Login |
| `usuarioId` | `1` | Al ejecutar Login |
| `productoId` | `1` | Manual |
| `compraId` | `1` | Manual |

El script de Login hace:

```javascript
const data = pm.response.json();
pm.collectionVariables.set('token', data.token);
pm.collectionVariables.set('usuarioId', data.id);
```

No existe `X-User-Role`: el rol viaja dentro del JWT firmado.

## 3. Flujo recomendado

### Paso 1: Registrar usuario

Carpeta **Auth** → **Registrar usuario**.

```json
{
  "nombre": "Ana Pérez",
  "email": "ana@ejemplo.com",
  "password": "secreto123",
  "telefono": "+573001234567",
  "edad": 28
}
```

Respuesta esperada: `201 Created` con `role: "USER"`.

### Paso 2: Iniciar sesión

Carpeta **Auth** → **Login**.

```json
{
  "email": "ana@ejemplo.com",
  "password": "secreto123"
}
```

Al recibir `200`, el script guarda `token` y `usuarioId`. Todas las peticiones heredan el Bearer automáticamente.

### Paso 3: Crear un administrador (solo la primera vez)

El registro no permite elegir rol. Promueve el usuario en PostgreSQL:

```bash
docker compose exec postgres psql -U postgres -d ecommerce \
  -c "UPDATE usuario SET role = 'ADMIN' WHERE email = 'ana@ejemplo.com';"
```

Vuelve a ejecutar **Login** para obtener un token con `role: "ADMIN"`. Sin este paso, crear/editar/eliminar productos devolverá `403`.

### Paso 4: Crear productos

Carpeta **Productos** → **Crear producto** (requiere token ADMIN).

```json
{
  "nombre": "Laptop Dell XPS 15",
  "descripcion": "Laptop de alto rendimiento",
  "precio": 1299.99,
  "stock": 10,
  "imagenUrl": "https://ejemplo.com/laptop.jpg"
}
```

Guarda el `id` devuelto en la variable `productoId` o ajusta la URL de los siguientes pasos.

### Paso 5: Consultar productos (público)

Carpeta **Productos** → **Listar productos**. No requiere token.

### Paso 6: Agregar al carrito

Carpeta **Carrito** → **Agregar producto**.

```json
{
  "productoId": 1,
  "cantidad": 2
}
```

La cantidad debe estar entre 1 y 10. El carrito no reserva stock.

### Paso 7: Ver y modificar el carrito

- **Ver carrito**: `GET /api/ecommerce/carrito`.
- **Actualizar cantidad**: `PUT /api/ecommerce/carrito/actualizar/1?cantidad=3`.
- **Eliminar producto**: `DELETE /api/ecommerce/carrito/eliminar/1`.
- **Vaciar carrito**: `DELETE /api/ecommerce/carrito/vaciar` (204).

### Paso 8: Realizar compra

Carpeta **Compras** → **Realizar compra**.

`POST /api/ecommerce/compras/realizar` sin cuerpo. El backend:

1. Descuenta el stock de forma atómica.
2. Guarda la compra.
3. Vacía el carrito.
4. Publica la notificación en RabbitMQ.

Respuesta `201 Created` con la compra y estado `COMPLETADA`.

### Paso 9: Ver compras

- **Compras del usuario**: `GET /api/ecommerce/compras/usuario/{{usuarioId}}`.
- **Compra por ID**: `GET /api/ecommerce/compras/{{compraId}}`.
- **Todas las compras** (ADMIN): `GET /api/ecommerce/compras`.

### Paso 10: Ver el correo y el SMS

- Correo: abre Mailpit en http://localhost:8025.
- SMS: si `SMS_PROVIDER=log`, revisa `docker compose logs -f notificaciones`.

## 4. Healthchecks

Carpeta **Health**:

- `GET {{auth_url}}/actuator/health`
- `GET {{catalogo_url}}/actuator/health`
- `GET http://localhost:9093/actuator/health`

Todos deben devolver `{"status":"UP"}`.

## 5. Códigos de respuesta

| Código | Significado |
| --- | --- |
| 200 | Operación exitosa |
| 201 | Recurso creado |
| 204 | Sin contenido |
| 400 | Datos inválidos o validación |
| 401 | Token ausente, inválido o expirado |
| 403 | Sin permisos (por ejemplo, crear producto sin ADMIN) |
| 404 | Recurso no encontrado |
| 409 | Conflicto (stock insuficiente, email duplicado) |
| 503 | Auth no disponible desde Catálogo |

## 6. Problemas comunes

### `401 No autenticado`

- Ejecuta primero **Login**.
- Verifica que la colección tenga seleccionada la autenticación **Bearer Token** con `{{token}}`.
- Si el token expiró, vuelve a hacer login.

### `403 Acceso denegado` al crear productos

El usuario no es `ADMIN`. Actualízalo en PostgreSQL y vuelve a iniciar sesión para obtener un token nuevo.

### `409 Stock insuficiente`

El stock cambió entre el carrito y el checkout. Consulta el producto y ajusta la cantidad.

### `ECONNREFUSED`

Los servicios no están corriendo. Verifica:

```bash
docker compose ps
curl http://localhost:9090/actuator/health
curl http://localhost:9092/actuator/health
```

### No se guarda el token

Abre la pestaña **Tests** del request de Login y revisa que el script no tenga errores. El script espera un `200` y un campo `token` en la respuesta.

Para el detalle de cada endpoint consulta [API.md](./API.md).
