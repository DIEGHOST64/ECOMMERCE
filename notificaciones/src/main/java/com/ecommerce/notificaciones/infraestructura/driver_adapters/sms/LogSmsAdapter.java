package com.ecommerce.notificaciones.infraestructura.driver_adapters.sms;

import com.ecommerce.notificaciones.domain.model.NotificacionSMS;
import com.ecommerce.notificaciones.domain.port.out.SmsGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Adaptador SMS por defecto para desarrollo: registra el mensaje en el log.
 * Se activa con {@code SMS_PROVIDER=log} o si no se configura proveedor.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.sms.provider", havingValue = "log", matchIfMissing = true)
public class LogSmsAdapter implements SmsGateway {

    @Override
    public void enviarSms(NotificacionSMS notificacion) {
        log.info("SMS simulado (SMS_PROVIDER=log) para {}: {}",
                notificacion.getNumeroTelefono(), notificacion.getMensaje());
    }
}
