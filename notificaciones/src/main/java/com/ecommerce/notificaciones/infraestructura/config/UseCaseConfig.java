package com.ecommerce.notificaciones.infraestructura.config;

import com.ecommerce.notificaciones.application.usecase.ProcesarNotificacionUseCase;
import com.ecommerce.notificaciones.domain.port.in.ProcesarNotificacionService;
import com.ecommerce.notificaciones.domain.port.out.EmailGateway;
import com.ecommerce.notificaciones.domain.port.out.SmsGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public ProcesarNotificacionService procesarNotificacionService(
            EmailGateway emailGateway, SmsGateway smsGateway) {
        return new ProcesarNotificacionUseCase(emailGateway, smsGateway);
    }
}
