package com.ecommerce.catalogo.application.port.in;

import com.ecommerce.catalogo.domain.model.Producto;

import java.util.List;

public interface ProductoService {

    Producto guardarProducto(Producto producto);

    Producto obtenerProducto(Long id);

    List<Producto> obtenerTodos();

    Producto actualizarProducto(Producto producto);

    void eliminarProducto(Long id);

    /** Descuenta stock o lanza {@code StockInsuficienteException}. */
    void descontarStock(Long productoId, int cantidad);
}
