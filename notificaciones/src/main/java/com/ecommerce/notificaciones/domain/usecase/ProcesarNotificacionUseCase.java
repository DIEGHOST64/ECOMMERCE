package com.ecommerce.notificaciones.domain.usecase;

import com.ecommerce.notificaciones.domain.gateway.EmailGateway;
import com.ecommerce.notificaciones.domain.gateway.SMSGateway;
import com.ecommerce.notificaciones.domain.model.MensajeCola;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ProcesarNotificacionUseCase {
    
    private final EmailGateway emailGateway;
    private final SMSGateway smsGateway;
    
    public void procesar(MensajeCola mensaje) {
        log.info("Procesando notificación de tipo: {}", mensaje.getTipo());
        
        try {
            switch (mensaje.getTipo()) {
                case EMAIL:
                    if (mensaje.getEmail() != null) {
                        emailGateway.enviarEmail(mensaje.getEmail());
                        log.info("Email enviado exitosamente a: {}", mensaje.getEmail().getDestinatario());
                    }
                    break;
                    
                case SMS:
                    if (mensaje.getSms() != null) {
                        smsGateway.enviarSMS(mensaje.getSms());
                        log.info("SMS enviado exitosamente a: {}", mensaje.getSms().getNumeroTelefono());
                    }
                    break;
                    
                case AMBOS:
                    if (mensaje.getEmail() != null) {
                        emailGateway.enviarEmail(mensaje.getEmail());
                        log.info("Email enviado exitosamente a: {}", mensaje.getEmail().getDestinatario());
                    }
                    if (mensaje.getSms() != null) {
                        smsGateway.enviarSMS(mensaje.getSms());
                        log.info("SMS enviado exitosamente a: {}", mensaje.getSms().getNumeroTelefono());
                    }
                    break;
            }
        } catch (Exception e) {
            log.error("Error al procesar notificación: {}", e.getMessage(), e);
            throw new RuntimeException("Error procesando notificación", e);
        }
    }
}
