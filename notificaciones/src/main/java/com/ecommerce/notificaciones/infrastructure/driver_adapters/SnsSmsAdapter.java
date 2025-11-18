package com.ecommerce.notificaciones.infrastructure.driver_adapters;

import com.ecommerce.notificaciones.domain.gateway.SMSGateway;
import com.ecommerce.notificaciones.domain.model.NotificacionSMS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class SnsSmsAdapter implements SMSGateway {
    
    private final SnsClient snsClient;
    
    @Value("${aws.sns.enabled}")
    private boolean snsEnabled;
    
    @Override
    public void enviarSMS(NotificacionSMS notificacion) {
        if (!snsEnabled) {
            log.warn("SNS está deshabilitado. SMS no enviado a: {}", notificacion.getNumeroTelefono());
            return;
        }
        
        try {
            // Formatear número de teléfono en formato E.164 (ejemplo: +573014346586)
            String numeroFormateado = formatearNumero(notificacion.getNumeroTelefono());
            
            PublishRequest request = PublishRequest.builder()
                    .message(notificacion.getMensaje())
                    .phoneNumber(numeroFormateado)
                    .build();
            
            PublishResponse response = snsClient.publish(request);
            log.info("SMS enviado con éxito. Message ID: {}", response.messageId());
            
        } catch (SnsException e) {
            log.error("Error enviando SMS con SNS: {}", e.awsErrorDetails().errorMessage(), e);
            throw new RuntimeException("Error enviando SMS", e);
        }
    }
    
    private String formatearNumero(String numero) {
        // Eliminar espacios y guiones
        String numeroLimpio = numero.replaceAll("[\\s-]", "");
        
        // Si no comienza con +, agregarlo (asumiendo Colombia +57)
        if (!numeroLimpio.startsWith("+")) {
            if (numeroLimpio.startsWith("57")) {
                numeroLimpio = "+" + numeroLimpio;
            } else {
                numeroLimpio = "+57" + numeroLimpio;
            }
        }
        
        return numeroLimpio;
    }
}
