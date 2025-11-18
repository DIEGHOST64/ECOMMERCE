package com.ecommerce.catalogo.domain.model.gateway;

import com.ecommerce.catalogo.domain.model.MensajeNotificacion;

public interface NotificacionGateway {
    void enviarNotificacion(MensajeNotificacion mensaje);
}
