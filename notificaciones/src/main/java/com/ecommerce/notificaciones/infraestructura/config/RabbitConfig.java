package com.ecommerce.notificaciones.infraestructura.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topologia RabbitMQ del servicio de notificaciones.
 *
 * <p>Exchange de trabajo + cola con reintentos y cola de mensajes muertos.
 * El tipo de mensaje se infiere del parametro del listener, por lo que los
 * productores no necesitan compartir clases Java con este servicio.</p>
 */
@Configuration
public class RabbitConfig {

    public static final String ROUTING_EMAIL = "notificacion.email";
    public static final String ROUTING_SMS = "notificacion.sms";
    public static final String ROUTING_AMBOS = "notificacion.ambos";

    @Bean
    public TopicExchange notificacionesExchange(
            @Value("${app.notificaciones.exchange:notificaciones.exchange}") String exchange) {
        return new TopicExchange(exchange, true, false);
    }

    @Bean
    public DirectExchange notificacionesDeadLetterExchange(
            @Value("${app.notificaciones.dlx:notificaciones.dlx}") String dlx) {
        return new DirectExchange(dlx, true, false);
    }

    @Bean
    public Queue notificacionesQueue(
            @Value("${app.notificaciones.queue:notificaciones.queue}") String queue,
            @Value("${app.notificaciones.dlx:notificaciones.dlx}") String dlx,
            @Value("${app.notificaciones.dlq:notificaciones.dlq}") String dlq) {
        return QueueBuilder.durable(queue)
                .deadLetterExchange(dlx)
                .deadLetterRoutingKey(dlq)
                .build();
    }

    @Bean
    public Queue notificacionesDeadLetterQueue(
            @Value("${app.notificaciones.dlq:notificaciones.dlq}") String dlq) {
        return QueueBuilder.durable(dlq).build();
    }

    @Bean
    public Binding bindingEmail(Queue notificacionesQueue, TopicExchange notificacionesExchange) {
        return BindingBuilder.bind(notificacionesQueue)
                .to(notificacionesExchange)
                .with(ROUTING_EMAIL);
    }

    @Bean
    public Binding bindingSms(Queue notificacionesQueue, TopicExchange notificacionesExchange) {
        return BindingBuilder.bind(notificacionesQueue)
                .to(notificacionesExchange)
                .with(ROUTING_SMS);
    }

    @Bean
    public Binding bindingAmbos(Queue notificacionesQueue, TopicExchange notificacionesExchange) {
        return BindingBuilder.bind(notificacionesQueue)
                .to(notificacionesExchange)
                .with(ROUTING_AMBOS);
    }

    @Bean
    public Binding bindingDeadLetter(Queue notificacionesDeadLetterQueue,
                                     DirectExchange notificacionesDeadLetterExchange,
                                     @Value("${app.notificaciones.dlq:notificaciones.dlq}") String dlq) {
        return BindingBuilder.bind(notificacionesDeadLetterQueue)
                .to(notificacionesDeadLetterExchange)
                .with(dlq);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        // Ignora el header __TypeId__ de otros servicios y usa el tipo del listener.
        typeMapper.setTypePrecedence(DefaultJackson2JavaTypeMapper.TypePrecedence.INFERRED);
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
