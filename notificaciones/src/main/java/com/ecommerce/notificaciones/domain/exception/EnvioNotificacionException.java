package com.ecommerce.notificaciones.domain.exception;

public class EnvioNotificacionException extends RuntimeException {

    public EnvioNotificacionException(String message) {
        super(message);
    }

    public EnvioNotificacionException(String message, Throwable cause) {
        super(message, cause);
    }
}
