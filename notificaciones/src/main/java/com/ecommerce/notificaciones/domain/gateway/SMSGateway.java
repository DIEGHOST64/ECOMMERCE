package com.ecommerce.notificaciones.domain.gateway;

import com.ecommerce.notificaciones.domain.model.NotificacionSMS;

public interface SMSGateway {
    void enviarSMS(NotificacionSMS notificacion);
}
