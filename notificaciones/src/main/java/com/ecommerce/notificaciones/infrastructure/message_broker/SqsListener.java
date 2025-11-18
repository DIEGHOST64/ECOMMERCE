package com.ecommerce.notificaciones.infrastructure.message_broker;

import com.ecommerce.notificaciones.domain.model.MensajeCola;
import com.ecommerce.notificaciones.domain.usecase.ProcesarNotificacionUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsListener {
    
    private final SqsClient sqsClient;
    private final ProcesarNotificacionUseCase procesarNotificacionUseCase;
    private final ObjectMapper objectMapper;
    
    @Value("${aws.sqs.queue.url}")
    private String queueUrl;
    
    @Value("${aws.sqs.maxNumberOfMessages}")
    private int maxNumberOfMessages;
    
    @Value("${aws.sqs.waitTimeSeconds}")
    private int waitTimeSeconds;
    
    @Scheduled(fixedDelay = 1000) // Ejecutar cada segundo
    public void escucharMensajes() {
        try {
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(maxNumberOfMessages)
                    .waitTimeSeconds(waitTimeSeconds)
                    .build();
            
            List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();
            
            for (Message message : messages) {
                procesarMensaje(message);
            }
            
        } catch (Exception e) {
            log.error("Error al escuchar mensajes de SQS: {}", e.getMessage(), e);
        }
    }
    
    private void procesarMensaje(Message message) {
        try {
            log.info("Mensaje recibido de SQS: {}", message.body());
            
            // Parsear el mensaje JSON
            MensajeCola mensajeCola = objectMapper.readValue(message.body(), MensajeCola.class);
            
            // Procesar la notificación
            procesarNotificacionUseCase.procesar(mensajeCola);
            
            // Eliminar el mensaje de la cola después de procesarlo exitosamente
            eliminarMensaje(message.receiptHandle());
            
        } catch (Exception e) {
            log.error("Error procesando mensaje de SQS: {}", e.getMessage(), e);
            // El mensaje no se elimina, volverá a estar disponible después del visibility timeout
        }
    }
    
    private void eliminarMensaje(String receiptHandle) {
        try {
            DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(receiptHandle)
                    .build();
            
            sqsClient.deleteMessage(deleteRequest);
            log.info("Mensaje eliminado de la cola SQS");
            
        } catch (SqsException e) {
            log.error("Error eliminando mensaje de SQS: {}", e.awsErrorDetails().errorMessage(), e);
        }
    }
}
