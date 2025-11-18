package com.ecommerce.notificaciones.application.config;

import com.ecommerce.notificaciones.domain.gateway.EmailGateway;
import com.ecommerce.notificaciones.domain.gateway.SMSGateway;
import com.ecommerce.notificaciones.domain.usecase.ProcesarNotificacionUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {
    
    @Bean
    public ProcesarNotificacionUseCase procesarNotificacionUseCase(EmailGateway emailGateway, SMSGateway smsGateway) {
        return new ProcesarNotificacionUseCase(emailGateway, smsGateway);
    }
}
