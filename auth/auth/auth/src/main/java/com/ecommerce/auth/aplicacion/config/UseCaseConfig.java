package com.ecommerce.auth.aplicacion.config;

import com.ecommerce.auth.domain.model.gateway.EncrypterGateway;
import com.ecommerce.auth.domain.model.gateway.NotificacionGateway;
import com.ecommerce.auth.domain.model.gateway.UsuarioGateway;
import com.ecommerce.auth.domain.usecase.UsuarioUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration

public class UseCaseConfig {
    @Bean
    public UsuarioUseCase usuarioUseCase(UsuarioGateway usuarioGateway, EncrypterGateway encrypterGateway, NotificacionGateway notificacionGateway) {
        return new UsuarioUseCase(usuarioGateway, encrypterGateway, notificacionGateway);
    }
}
