# Módulo de carrito

Documentación del carrito del servicio de Catálogo. La referencia completa de la API está en [API.md](../../API.md#carrito).

## Modelo

- **Carrito**: un carrito por usuario (`usuario_id` único), con `precioTotal`.
- **ItemCarrito**: producto, nombre, precio unitario, cantidad, subtotal e imagen.
- El carrito se identifica por el usuario del JWT; no se acepta `usuarioId` por URL ni query.

## Endpoints

| Método | Endpoint | Descripción |
| --- | --- | --- |
| GET | `/api/ecommerce/carrito` | Ver el carrito del usuario autenticado |
| POST | `/api/ecommerce/carrito/agregar` | Agregar `{ productoId, cantidad }` |
| PUT | `/api/ecommerce/carrito/actualizar/{productoId}?cantidad=N` | Cambiar la cantidad |
| DELETE | `/api/ecommerce/carrito/eliminar/{productoId}` | Eliminar un producto |
| DELETE | `/api/ecommerce/carrito/vaciar` | Vaciar el carrito (204) |

Todos requieren `Authorization: Bearer <token>`.

## Reglas de negocio

- La cantidad debe estar entre 1 y 10 por operación.
- El producto debe existir.
- **El carrito no reserva stock.** Solo el checkout valida y descuenta inventario.
- Agregar un producto que ya está en el carrito suma la cantidad.
- El total y los subtotales se recalculan en el servidor.
- La identidad y la propiedad se derivan del JWT.

## Flujo

```text
1. El usuario agrega productos al carrito.
2. Puede modificar cantidades o eliminar líneas.
3. Al confirmar la compra, Catálogo descuenta el stock de forma atómica.
4. Si algún producto no tiene stock, la compra falla y el carrito se conserva.
5. Si la compra se guarda, el carrito se vacía y se publica la notificación.
```

## Respuesta típica

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

## Errores

| Código | Caso |
| --- | --- |
| 400 | Cantidad inválida o carrito vacío |
| 401 | Token ausente, inválido o expirado |
| 403 | El token no corresponde al usuario |
| 404 | Producto no encontrado |
| 409 | Stock insuficiente en el checkout |
| 503 | Auth no disponible |

## Capas

- `domain/model/Carrito`, `domain/model/ItemCarrito`
- `domain/port/in/CarritoService`
- `domain/port/out/CarritoGateway`, `ProductoGateway`, `UsuarioGateway`
- `application/usecase/CarritoUseCase`
- `infraestructura/entry_points/CarritoController`
- `infraestructura/driver_adapters/jpa_repository/carrito`

El adaptador JPA no se usa desde el caso de uso: este depende de `CarritoGateway`.
