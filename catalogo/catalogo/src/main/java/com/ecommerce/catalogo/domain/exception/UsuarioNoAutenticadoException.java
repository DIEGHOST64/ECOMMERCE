package com.ecommerce.catalogo.domain.exception;

public class UsuarioNoAutenticadoException extends RuntimeException {

    public UsuarioNoAutenticadoException(String message) {
        super(message);
    }
}
