package com.ecommerce.notificaciones.domain.port.out;

import com.ecommerce.notificaciones.domain.model.NotificacionSMS;

public interface SmsGateway {

    void enviarSms(NotificacionSMS notificacion);
}
