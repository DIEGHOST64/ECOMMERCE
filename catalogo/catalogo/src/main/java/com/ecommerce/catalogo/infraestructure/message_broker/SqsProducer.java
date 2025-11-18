package com.ecommerce.catalogo.infraestructure.message_broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsProducer {
    
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    
    @Value("${aws.sqs.queue.url:}")
    private String queueUrl;
    
    public void enviarMensaje(Object mensaje) {
        if (queueUrl == null || queueUrl.isEmpty()) {
            log.warn("SQS Queue URL no configurada. Mensaje no enviado.");
            return;
        }
        
        try {
            String mensajeJson = objectMapper.writeValueAsString(mensaje);
            
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(mensajeJson)
                    .build();
            
            sqsClient.sendMessage(sendMsgRequest);
            log.info("Mensaje enviado a SQS: {}", mensajeJson);
            
        } catch (Exception e) {
            log.error("Error enviando mensaje a SQS: {}", e.getMessage(), e);
        }
    }
}
