package com.ecommerce.catalogo.domain.port.out;

import com.ecommerce.catalogo.domain.model.MensajeNotificacion;

public interface NotificacionGateway {
    void enviarNotificacion(MensajeNotificacion mensaje);
}
