package com.ecommerce.notificaciones.infrastructure.driver_adapters;

import com.ecommerce.notificaciones.domain.gateway.EmailGateway;
import com.ecommerce.notificaciones.domain.model.NotificacionEmail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class SesEmailAdapter implements EmailGateway {
    
    private final SesClient sesClient;
    
    @Value("${aws.ses.fromEmail}")
    private String fromEmail;
    
    @Value("${aws.ses.fromName}")
    private String fromName;
    
    @Override
    public void enviarEmail(NotificacionEmail notificacion) {
        try {
            Content subject = Content.builder()
                    .data(notificacion.getAsunto())
                    .build();
            
            Content bodyContent = Content.builder()
                    .data(notificacion.getCuerpo())
                    .build();
            
            Body body;
            if (notificacion.isHtml()) {
                body = Body.builder()
                        .html(bodyContent)
                        .build();
            } else {
                body = Body.builder()
                        .text(bodyContent)
                        .build();
            }
            
            Message message = Message.builder()
                    .subject(subject)
                    .body(body)
                    .build();
            
            Destination destination = Destination.builder()
                    .toAddresses(notificacion.getDestinatario())
                    .build();
            
            SendEmailRequest emailRequest = SendEmailRequest.builder()
                    .destination(destination)
                    .message(message)
                    .source(fromName + " <" + fromEmail + ">")
                    .build();
            
            SendEmailResponse response = sesClient.sendEmail(emailRequest);
            log.info("Email enviado con éxito. Message ID: {}", response.messageId());
            
        } catch (SesException e) {
            log.error("Error enviando email con SES: {}", e.awsErrorDetails().errorMessage(), e);
            throw new RuntimeException("Error enviando email", e);
        }
    }
}
