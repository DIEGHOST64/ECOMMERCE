package com.ecommerce.auth.domain.model.gateway;

import com.ecommerce.auth.domain.model.MensajeNotificacion;

public interface NotificacionGateway {
    void enviarNotificacion(MensajeNotificacion mensaje);
}
