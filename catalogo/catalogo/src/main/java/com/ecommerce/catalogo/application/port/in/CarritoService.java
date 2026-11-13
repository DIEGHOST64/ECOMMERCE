package com.ecommerce.catalogo.application.port.in;

import com.ecommerce.catalogo.domain.model.Carrito;

public interface CarritoService {

    Carrito agregarProductoAlCarrito(Long usuarioId, Long productoId, int cantidad);

    Carrito actualizarCantidadProducto(Long usuarioId, Long productoId, int nuevaCantidad);

    Carrito eliminarProducto(Long usuarioId, Long productoId);

    Carrito vaciarCarrito(Long usuarioId);

    Carrito obtenerCarrito(Long usuarioId);
}
