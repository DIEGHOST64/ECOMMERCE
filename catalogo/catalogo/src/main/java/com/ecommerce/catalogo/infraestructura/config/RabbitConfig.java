package com.ecommerce.catalogo.infraestructura.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ reemplaza AWS SQS. El productor solo declara el exchange;
 * la cola y el DLQ pertenecen al servicio de notificaciones.
 */
@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange notificacionesExchange(
            @Value("${app.notificaciones.exchange:notificaciones.exchange}") String exchange) {
        return new TopicExchange(exchange, true, false);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
