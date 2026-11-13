package com.ecommerce.notificaciones.domain.port.out;

import com.ecommerce.notificaciones.domain.model.NotificacionEmail;

public interface EmailGateway {

    void enviarEmail(NotificacionEmail notificacion);
}
