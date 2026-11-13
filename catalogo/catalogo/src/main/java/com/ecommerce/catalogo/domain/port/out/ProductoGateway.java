package com.ecommerce.catalogo.domain.port.out;

import com.ecommerce.catalogo.domain.model.Producto;

import java.util.List;

public interface ProductoGateway {

    Producto guardar(Producto producto);

    /** Retorna {@code null} cuando el producto no existe. */
    Producto buscarPorId(Long id);

    List<Producto> obtenerTodos();

    void eliminar(Long id);

    /**
     * Descuenta stock de forma atomica.
     *
     * @return {@code true} si habia stock suficiente y se desconto;
     *         {@code false} en caso contrario.
     */
    boolean descontarStock(Long productoId, int cantidad);
}
