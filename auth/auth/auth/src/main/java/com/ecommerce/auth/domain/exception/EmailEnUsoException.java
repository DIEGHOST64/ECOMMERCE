package com.ecommerce.auth.domain.exception;

public class EmailEnUsoException extends RuntimeException {

    public EmailEnUsoException(String email) {
        super("El email " + email + " ya esta registrado");
    }
}
