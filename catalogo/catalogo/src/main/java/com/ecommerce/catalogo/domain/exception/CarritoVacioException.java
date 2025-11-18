package com.ecommerce.catalogo.domain.exception;

public class CarritoVacioException extends RuntimeException {
    public CarritoVacioException(String message) {
        super(message);
    }
}