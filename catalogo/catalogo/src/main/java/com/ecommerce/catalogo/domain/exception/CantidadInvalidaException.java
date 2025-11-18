package com.ecommerce.catalogo.domain.exception;

public class CantidadInvalidaException extends RuntimeException {
    public CantidadInvalidaException(String message) {
        super(message);
    }
}