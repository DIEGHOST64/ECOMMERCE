package com.ecommerce.notificaciones.infraestructura.messaging;

import com.ecommerce.notificaciones.domain.model.MensajeNotificacion;
import com.ecommerce.notificaciones.domain.port.in.ProcesarNotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Adaptador de entrada: consume los mensajes publicados por auth y catalogo.
 *
 * <p>Los fallos se propagan deliberadamente para que el contenedor de RabbitMQ
 * aplique la politica de reintentos y, agotados estos, derive a la DLQ.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacionListener {

    private final ProcesarNotificacionService procesarNotificacionService;

    @RabbitListener(queues = "${app.notificaciones.queue:notificaciones.queue}")
    public void escuchar(MensajeNotificacion mensaje) {
        log.info("Notificacion recibida: tipo={}", mensaje == null ? null : mensaje.getTipo());
        procesarNotificacionService.procesar(mensaje);
    }
}
