# Documentación del Módulo Carrito de Compras

## ¿Qué es el carrito de compras?
El carrito de compras es una funcionalidad que permite a los usuarios de la tienda agregar productos, modificar cantidades, eliminar productos y vaciar el carrito antes de realizar una compra. Es el "centro de operaciones" para la experiencia de compra online.

---

## Componentes principales

### 1. Modelos
- **Carrito**: Representa el carrito de un usuario. Tiene un ID, el ID del usuario, una lista de productos (items) y el precio total.
- **ItemCarrito**: Representa cada producto dentro del carrito, con su cantidad, precio unitario y subtotal.

### 2. Controlador REST (`CarritoController`)
Expone los endpoints para interactuar con el carrito:
- **GET /api/ecommerce/carrito?usuarioId=1**: Ver el estado actual del carrito.
- **POST /api/ecommerce/carrito/agregar**: Agregar un producto al carrito.
- **PUT /api/ecommerce/carrito/actualizar/{productoId}**: Modificar la cantidad de un producto en el carrito.
- **DELETE /api/ecommerce/carrito/eliminar/{productoId}**: Eliminar un producto del carrito.
- **DELETE /api/ecommerce/carrito/vaciar**: Vaciar el carrito por completo.

### 3. Casos de uso (`CarritoUseCase`)
Contiene la lógica de negocio:
- Validación de usuario y producto.
- Control de stock (no permite agregar más productos de los que hay disponibles).
- Validación de cantidades (no permite cantidades negativas, ni más de 10 por producto).
- Actualización de stock al agregar, quitar o modificar productos.
- Cálculo de subtotales y total del carrito.

### 4. Repositorios
- **CarritoGateway**: Interfaz para acceder y modificar carritos en la base de datos.
- **CarritoDataJpaRepository**: Implementación JPA para persistencia.

### 5. Excepciones
- **UsuarioNoEncontradoException**: El usuario no existe.
- **StockInsuficienteException**: No hay suficiente stock para el producto solicitado.
- **CantidadInvalidaException**: La cantidad solicitada no es válida.
- **CarritoVacioException**: El carrito está vacío.

---

## Flujo de trabajo

1. **Ver el carrito**
   - El usuario consulta el estado actual de su carrito.
   - Si el usuario no existe, se devuelve un error.
   - Si el carrito está vacío, se muestra vacío.

2. **Agregar producto**
   - El usuario agrega un producto y una cantidad.
   - El sistema valida usuario, cantidad y stock.
   - Si todo es correcto, se agrega el producto y se actualiza el stock.

3. **Actualizar cantidad**
   - El usuario modifica la cantidad de un producto en el carrito.
   - El sistema valida usuario, cantidad y stock.
   - Si aumenta la cantidad, se descuenta del stock; si disminuye, se devuelve al stock.

4. **Eliminar producto**
   - El usuario elimina un producto del carrito.
   - El sistema devuelve la cantidad eliminada al stock y recalcula el total.

5. **Vaciar carrito**
   - El usuario elimina todos los productos del carrito.
   - El sistema devuelve todas las cantidades al stock y deja el carrito vacío.

---

## Validaciones y seguridad
- Solo usuarios válidos pueden operar sobre su carrito.
- No se puede agregar más productos de los que hay en stock.
- No se puede pedir cantidades negativas o mayores a 10 por producto.
- El sistema maneja errores y devuelve mensajes claros para cada caso.

---

## Ejemplo de uso

1. **Ver carrito**
   ```http
   GET /api/ecommerce/carrito?usuarioId=1
   ```
2. **Agregar producto**
   ```http
   POST /api/ecommerce/carrito/agregar?usuarioId=1
   {
     "productoId": 1,
     "cantidad": 4
   }
   ```
3. **Actualizar cantidad**
   ```http
   PUT /api/ecommerce/carrito/actualizar/1?usuarioId=1&cantidad=2
   ```
4. **Eliminar producto**
   ```http
   DELETE /api/ecommerce/carrito/eliminar/1?usuarioId=1
   ```
5. **Vaciar carrito**
   ```http
   DELETE /api/ecommerce/carrito/vaciar?usuarioId=1
   ```

---

## Respuestas típicas
- Carrito con productos:
  ```json
  {
    "id": 1,
    "usuarioId": 1,
    "items": [ ... ],
    "precioTotal": 200.00
  }
  ```
- Carrito vacío:
  ```json
  {
    "id": 1,
    "usuarioId": 1,
    "items": [],
    "precioTotal": 0.0
  }
  ```
- Error de usuario:
  ```json
  {
    "mensaje": "Usuario no encontrado con ID: 1"
  }
  ```
- Error de stock:
  ```json
  {
    "mensaje": "Stock insuficiente. Stock disponible: 3"
  }
  ```

---

## Resumen
Este módulo permite gestionar el carrito de compras de forma segura, eficiente y robusta, garantizando la integridad del stock y la experiencia del usuario.
