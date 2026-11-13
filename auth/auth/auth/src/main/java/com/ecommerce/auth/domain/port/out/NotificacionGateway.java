package com.ecommerce.auth.domain.port.out;

import com.ecommerce.auth.domain.model.MensajeNotificacion;

public interface NotificacionGateway {
    void enviarNotificacion(MensajeNotificacion mensaje);
}
