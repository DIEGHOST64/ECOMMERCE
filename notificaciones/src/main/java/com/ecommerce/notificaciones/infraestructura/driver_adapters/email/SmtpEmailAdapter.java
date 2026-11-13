package com.ecommerce.notificaciones.infraestructura.driver_adapters.email;

import com.ecommerce.notificaciones.domain.exception.EnvioNotificacionException;
import com.ecommerce.notificaciones.domain.model.NotificacionEmail;
import com.ecommerce.notificaciones.domain.port.out.EmailGateway;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Adaptador de salida para email via SMTP (Mailpit en desarrollo, cualquier
 * servidor SMTP en produccion). Reemplaza AWS SES.
 */
@Slf4j
@Component
public class SmtpEmailAdapter implements EmailGateway {

    private final JavaMailSender mailSender;
    private final String from;
    private final String fromName;

    public SmtpEmailAdapter(JavaMailSender mailSender,
                            @Value("${app.mail.from:no-reply@ecommerce.local}") String from,
                            @Value("${app.mail.from-name:E-Commerce}") String fromName) {
        this.mailSender = mailSender;
        this.from = from;
        this.fromName = fromName;
    }

    @Override
    public void enviarEmail(NotificacionEmail notificacion) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, false, StandardCharsets.UTF_8.name());

            helper.setFrom(new InternetAddress(from, fromName, StandardCharsets.UTF_8.name()));
            helper.setTo(notificacion.getDestinatario());
            helper.setSubject(notificacion.getAsunto());
            helper.setText(notificacion.getCuerpo(), notificacion.isHtml());

            mailSender.send(message);
            log.info("Email SMTP enviado a {}", notificacion.getDestinatario());
        } catch (Exception e) {
            throw new EnvioNotificacionException(
                    "No se pudo enviar el email a " + notificacion.getDestinatario(), e);
        }
    }
}
