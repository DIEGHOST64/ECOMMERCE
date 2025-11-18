package com.ecommerce.notificaciones.domain.gateway;

import com.ecommerce.notificaciones.domain.model.NotificacionEmail;

public interface EmailGateway {
    void enviarEmail(NotificacionEmail notificacion);
}
