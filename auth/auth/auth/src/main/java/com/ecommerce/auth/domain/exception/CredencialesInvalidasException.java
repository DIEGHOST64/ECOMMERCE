package com.ecommerce.auth.domain.exception;

public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException() {
        super("Email o contrasena incorrectos");
    }
}
