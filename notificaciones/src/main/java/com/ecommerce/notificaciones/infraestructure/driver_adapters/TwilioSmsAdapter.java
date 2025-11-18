package com.ecommerce.notificaciones.infraestructure.driver_adapters;

import com.ecommerce.notificaciones.domain.gateway.SMSGateway;
import com.ecommerce.notificaciones.domain.model.NotificacionSMS;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Primary
public class TwilioSmsAdapter implements SMSGateway {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    private boolean initialized = false;

    private void initializeTwilio() {
        if (!initialized) {
            Twilio.init(accountSid, authToken);
            initialized = true;
            log.info("Twilio inicializado correctamente");
        }
    }

    @Override
    public void enviarSMS(NotificacionSMS notificacion) {
        try {
            initializeTwilio();
            
            String numeroTelefono = notificacion.getNumeroTelefono();
            String mensaje = notificacion.getMensaje();
            
            log.info("Enviando SMS a {} usando Twilio", numeroTelefono);
            
            Message message = Message.creator(
                    new PhoneNumber(numeroTelefono),    // To
                    new PhoneNumber(twilioPhoneNumber),  // From
                    mensaje                               // Body
            ).create();

            log.info("SMS enviado con éxito. Message SID: {}", message.getSid());
            
        } catch (Exception e) {
            log.error("Error al enviar SMS con Twilio: {}", e.getMessage(), e);
            throw new RuntimeException("Error al enviar SMS: " + e.getMessage());
        }
    }
}
