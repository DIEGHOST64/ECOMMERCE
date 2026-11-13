package com.ecommerce.notificaciones.infraestructura.driver_adapters.email;

import com.ecommerce.notificaciones.domain.exception.EnvioNotificacionException;
import com.ecommerce.notificaciones.domain.model.NotificacionEmail;
import jakarta.mail.BodyPart;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SmtpEmailAdapterTest {

    @Mock
    private JavaMailSender mailSender;

    @Test
    void enviaEmailHtmlConRemitenteYDestinatario() throws Exception {
        when(mailSender.createMimeMessage())
                .thenReturn(new MimeMessage(Session.getInstance(new Properties())));

        SmtpEmailAdapter adapter = new SmtpEmailAdapter(
                mailSender, "no-reply@ecommerce.local", "E-Commerce");

        adapter.enviarEmail(NotificacionEmail.builder()
                .destinatario("ana@example.com")
                .asunto("Bienvenido")
                .cuerpo("<h1>Hola Ana</h1>")
                .html(true)
                .build());

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());

        MimeMessage enviado = captor.getValue();
        // El transporte SMTP real llama saveChanges() antes de enviar.
        enviado.saveChanges();
        assertEquals("Bienvenido", enviado.getSubject());
        assertEquals("no-reply@ecommerce.local",
                ((InternetAddress) enviado.getFrom()[0]).getAddress());
        assertEquals("E-Commerce",
                ((InternetAddress) enviado.getFrom()[0]).getPersonal());
        assertEquals("ana@example.com",
                ((InternetAddress) enviado.getAllRecipients()[0]).getAddress());

        Object contenido = enviado.getContent();
        if (contenido instanceof MimeMultipart multipart) {
            BodyPart parte = multipart.getBodyPart(0);
            assertTrue(parte.isMimeType("text/html"), "contentType=" + parte.getContentType());
            assertTrue(parte.getContent().toString().contains("Hola Ana"));
        } else {
            assertTrue(enviado.isMimeType("text/html"), "contentType=" + enviado.getContentType());
            assertTrue(contenido.toString().contains("Hola Ana"));
        }
    }

    @Test
    void envuelveLosErroresSmtpEnExcepcionDeDominio() {
        when(mailSender.createMimeMessage())
                .thenReturn(new MimeMessage(Session.getInstance(new Properties())));
        doThrow(new MailSendException("SMTP caido"))
                .when(mailSender).send(any(MimeMessage.class));

        SmtpEmailAdapter adapter = new SmtpEmailAdapter(
                mailSender, "no-reply@ecommerce.local", "E-Commerce");

        assertThrows(EnvioNotificacionException.class, () -> adapter.enviarEmail(
                NotificacionEmail.builder()
                        .destinatario("ana@example.com")
                        .asunto("Bienvenido")
                        .cuerpo("Hola")
                        .html(false)
                        .build()));
    }
}
