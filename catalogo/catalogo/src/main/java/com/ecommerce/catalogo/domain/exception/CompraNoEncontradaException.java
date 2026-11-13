package com.ecommerce.catalogo.domain.exception;

public class CompraNoEncontradaException extends RuntimeException {

    public CompraNoEncontradaException(Long id) {
        super("No existe una compra con id " + id);
    }
}
