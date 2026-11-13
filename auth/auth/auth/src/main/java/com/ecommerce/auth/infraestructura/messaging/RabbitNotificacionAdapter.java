package com.ecommerce.auth.infraestructura.messaging;

import com.ecommerce.auth.domain.model.MensajeNotificacion;
import com.ecommerce.auth.domain.model.NotificacionEmail;
import com.ecommerce.auth.domain.model.NotificacionSMS;
import com.ecommerce.auth.domain.port.out.NotificacionGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida que publica notificaciones en RabbitMQ.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitNotificacionAdapter implements NotificacionGateway {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.notificaciones.exchange:notificaciones.exchange}")
    private String exchange;

    @Override
    public void enviarNotificacion(MensajeNotificacion mensaje) {
        if (mensaje == null || mensaje.getTipo() == null) {
            throw new IllegalArgumentException("El mensaje de notificacion es obligatorio");
        }

        NotificacionEvent event = new NotificacionEvent(
                mensaje.getTipo().name(),
                toEmail(mensaje.getEmail()),
                toSms(mensaje.getSms()));

        rabbitTemplate.convertAndSend(exchange, routingKey(mensaje.getTipo()), event);
        log.info("Notificacion {} publicada en RabbitMQ para {}",
                mensaje.getTipo(), event.destinatario());
    }

    private static String routingKey(MensajeNotificacion.TipoNotificacion tipo) {
        return switch (tipo) {
            case EMAIL -> "notificacion.email";
            case SMS -> "notificacion.sms";
            case AMBOS -> "notificacion.ambos";
        };
    }

    private static EmailPayload toEmail(NotificacionEmail email) {
        if (email == null) {
            return null;
        }
        return new EmailPayload(email.getDestinatario(), email.getAsunto(),
                email.getCuerpo(), email.isHtml());
    }

    private static SmsPayload toSms(NotificacionSMS sms) {
        if (sms == null) {
            return null;
        }
        return new SmsPayload(sms.getNumeroTelefono(), sms.getMensaje());
    }

    /**
     * Contrato JSON compartido con el servicio de notificaciones.
     */
    public record NotificacionEvent(String tipo, EmailPayload email, SmsPayload sms) {

        public String destinatario() {
            if (email != null) {
                return email.destinatario();
            }
            return sms != null ? sms.numeroTelefono() : "desconocido";
        }
    }

    public record EmailPayload(String destinatario, String asunto, String cuerpo, boolean html) {
    }

    public record SmsPayload(String numeroTelefono, String mensaje) {
    }
}
