package com.ecommerce.catalogo.infraestructure.driver_adapters;

import com.ecommerce.catalogo.domain.model.MensajeNotificacion;
import com.ecommerce.catalogo.domain.model.gateway.NotificacionGateway;
import com.ecommerce.catalogo.infraestructure.message_broker.MensajeCola;
import com.ecommerce.catalogo.infraestructure.message_broker.NotificacionEmail;
import com.ecommerce.catalogo.infraestructure.message_broker.NotificacionSMS;
import com.ecommerce.catalogo.infraestructure.message_broker.SqsProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SqsNotificacionAdapter implements NotificacionGateway {

    private final SqsProducer sqsProducer;

    @Override
    public void enviarNotificacion(MensajeNotificacion mensaje) {
        // Convertir de modelo de dominio a modelo de infraestructura
        MensajeCola mensajeCola = MensajeCola.builder()
                .tipo(convertirTipo(mensaje.getTipo()))
                .email(convertirEmail(mensaje.getEmail()))
                .sms(convertirSms(mensaje.getSms()))
                .build();
        
        sqsProducer.enviarMensaje(mensajeCola);
    }
    
    private MensajeCola.TipoNotificacion convertirTipo(MensajeNotificacion.TipoNotificacion tipo) {
        if (tipo == null) return null;
        return switch (tipo) {
            case EMAIL -> MensajeCola.TipoNotificacion.EMAIL;
            case SMS -> MensajeCola.TipoNotificacion.SMS;
            case AMBOS -> MensajeCola.TipoNotificacion.AMBOS;
        };
    }
    
    private NotificacionEmail convertirEmail(com.ecommerce.catalogo.domain.model.NotificacionEmail email) {
        if (email == null) return null;
        return NotificacionEmail.builder()
                .destinatario(email.getDestinatario())
                .asunto(email.getAsunto())
                .cuerpo(email.getCuerpo())
                .html(email.isHtml())
                .build();
    }
    
    private NotificacionSMS convertirSms(com.ecommerce.catalogo.domain.model.NotificacionSMS sms) {
        if (sms == null) return null;
        return NotificacionSMS.builder()
                .numeroTelefono(sms.getNumeroTelefono())
                .mensaje(sms.getMensaje())
                .build();
    }
}
