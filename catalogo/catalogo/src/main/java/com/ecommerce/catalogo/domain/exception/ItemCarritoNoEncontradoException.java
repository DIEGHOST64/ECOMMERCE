package com.ecommerce.catalogo.domain.exception;

public class ItemCarritoNoEncontradoException extends RuntimeException {

    public ItemCarritoNoEncontradoException(Long productoId) {
        super("El producto " + productoId + " no esta en el carrito");
    }
}
