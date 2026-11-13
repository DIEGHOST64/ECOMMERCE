package com.ecommerce.auth.domain.exception;

public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException(Long id) {
        super("No existe un usuario con id " + id);
    }

    public UsuarioNoEncontradoException(String message) {
        super(message);
    }
}
