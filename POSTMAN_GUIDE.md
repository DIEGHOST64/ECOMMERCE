# 📘 Guía de Uso de Postman Collection

## 🚀 Importar la Colección

1. Abre Postman
2. Click en **Import**
3. Selecciona el archivo `POSTMAN_COLLECTION.json`
4. La colección "E-Commerce Microservices API" estará lista para usar

## 🔧 Configurar Variables

La colección incluye variables predefinidas:

- `auth_url`: http://localhost:9090
- `catalogo_url`: http://localhost:9092
- `token`: (se llena automáticamente al hacer login)
- `usuarioId`: 1 (se actualiza automáticamente al hacer login)

## 📝 Flujo de Prueba Recomendado

### 1️⃣ Crear Usuario
**Endpoint:** `Auth Service > Registrar Usuario`

```json
{
  "username": "testuser",
  "password": "password123",
  "email": "test@example.com",
  "nombre": "Usuario Test",
  "telefono": "+1234567890"
}
```

### 2️⃣ Iniciar Sesión
**Endpoint:** `Auth Service > Login`

```json
{
  "username": "testuser",
  "password": "password123"
}
```

✨ El token y el usuarioId se guardan automáticamente en las variables de la colección.

### 3️⃣ Crear Productos
**Endpoint:** `Productos > Crear Producto`

```json
{
  "nombre": "Laptop Dell XPS 15",
  "descripcion": "Laptop de alto rendimiento",
  "precio": 1299.99,
  "stock": 10,
  "imagenUrl": "https://example.com/laptop.jpg"
}
```

Repite este paso para crear varios productos.

### 4️⃣ Listar Productos
**Endpoint:** `Productos > Listar Todos los Productos`

Verás todos los productos creados.

### 5️⃣ Agregar al Carrito
**Endpoint:** `Carrito > Agregar Producto al Carrito`

```json
{
  "productoId": 1,
  "cantidad": 2
}
```

### 6️⃣ Ver Carrito
**Endpoint:** `Carrito > Ver Carrito`

Verás el contenido de tu carrito con el total calculado.

### 7️⃣ Actualizar Cantidad
**Endpoint:** `Carrito > Actualizar Cantidad`

Modifica la cantidad de un producto en el carrito.

### 8️⃣ Realizar Compra
**Endpoint:** `Compras > Realizar Compra`

Convierte el carrito en una compra confirmada.

### 9️⃣ Ver Historial de Compras
**Endpoint:** `Compras > Obtener Compras por Usuario`

Consulta todas tus compras realizadas.

## 📋 Endpoints Disponibles

### 🔐 Auth Service (Puerto 9090)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/ecommerce/usuario/save` | Registrar usuario |
| POST | `/api/ecommerce/usuario/login` | Iniciar sesión |
| GET | `/api/ecommerce/usuario/{id}` | Obtener usuario |
| GET | `/api/ecommerce/usuario/{id}/existe` | Verificar existencia |
| PUT | `/api/ecommerce/usuario/update` | Actualizar usuario |
| DELETE | `/api/ecommerce/usuario/{id}` | Eliminar usuario |

### 🛍️ Productos (Puerto 9092)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/ecommerce/producto` | Listar productos |
| GET | `/api/ecommerce/producto/{id}` | Obtener producto |
| POST | `/api/ecommerce/producto` | Crear producto |
| PUT | `/api/ecommerce/producto/{id}` | Actualizar producto |
| DELETE | `/api/ecommerce/producto/{id}` | Eliminar producto |

### 🛒 Carrito (Puerto 9092)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/ecommerce/carrito?usuarioId={id}` | Ver carrito |
| POST | `/api/ecommerce/carrito/agregar?usuarioId={id}` | Agregar producto |
| PUT | `/api/ecommerce/carrito/actualizar/{productoId}?usuarioId={id}&cantidad={n}` | Actualizar cantidad |
| DELETE | `/api/ecommerce/carrito/eliminar/{productoId}?usuarioId={id}` | Eliminar producto |
| DELETE | `/api/ecommerce/carrito/vaciar?usuarioId={id}` | Vaciar carrito |

### 💳 Compras (Puerto 9092)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/ecommerce/compras/realizar?usuarioId={id}` | Realizar compra |
| GET | `/api/ecommerce/compras/usuario/{usuarioId}` | Compras por usuario |
| GET | `/api/ecommerce/compras/{compraId}` | Obtener compra |
| GET | `/api/ecommerce/compras` | Todas las compras (Admin) |

## ⚠️ Códigos de Respuesta

| Código | Significado |
|--------|-------------|
| 200 | Operación exitosa |
| 201 | Recurso creado |
| 400 | Petición incorrecta |
| 404 | Recurso no encontrado |
| 409 | Conflicto (ej: usuario ya existe) |
| 500 | Error interno del servidor |

## 💡 Tips

1. **Orden de prueba**: Siempre ejecuta Login primero para obtener el token
2. **Variables automáticas**: El token y usuarioId se actualizan solos tras el login
3. **IDs dinámicos**: Ajusta los IDs de productos/carritos según tus datos
4. **Errores comunes**: 
   - Si obtienes 404, verifica que el microservicio esté corriendo
   - Si obtienes 400, revisa el formato del JSON

## 🐛 Solución de Problemas

### Error de conexión
```
Error: connect ECONNREFUSED
```
**Solución:** Verifica que los servicios estén corriendo en los puertos correctos:
- Auth: http://localhost:9090
- Catálogo: http://localhost:9092

### Usuario no encontrado
```
{
  "error": "Usuario no encontrado con ID: 1"
}
```
**Solución:** Asegúrate de haber creado un usuario primero y de usar el ID correcto.

### Stock insuficiente
```
{
  "error": "No hay stock suficiente para el producto ID: 1"
}
```
**Solución:** Verifica que el producto tenga stock disponible antes de agregarlo al carrito.

---

**¡Disfruta probando la API! 🎉**
