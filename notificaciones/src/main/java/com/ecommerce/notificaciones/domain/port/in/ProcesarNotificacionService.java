package com.ecommerce.notificaciones.domain.port.in;

import com.ecommerce.notificaciones.domain.model.MensajeNotificacion;

/**
 * Caso de uso: procesa un mensaje de notificacion recibido desde RabbitMQ.
 */
public interface ProcesarNotificacionService {

    /**
     * Envia las notificaciones indicadas por el mensaje.
     *
     * @throws com.ecommerce.notificaciones.domain.exception.DatosInvalidosException
     *         si el mensaje no contiene los datos minimos.
     * @throws com.ecommerce.notificaciones.domain.exception.EnvioNotificacionException
     *         si algun proveedor falla; la excepcion se propaga para que RabbitMQ
     *         reintente y, agotados los intentos, derive el mensaje a la DLQ.
     */
    void procesar(MensajeNotificacion mensaje);
}
