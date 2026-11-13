package com.ecommerce.auth.infraestructura.config;

import com.ecommerce.auth.domain.port.out.EncrypterGateway;
import com.ecommerce.auth.domain.port.out.NotificacionGateway;
import com.ecommerce.auth.domain.port.out.TokenGateway;
import com.ecommerce.auth.domain.port.out.UsuarioGateway;
import com.ecommerce.auth.application.port.in.UsuarioService;
import com.ecommerce.auth.application.usecase.UsuarioUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public UsuarioService usuarioService(UsuarioGateway usuarioGateway,
                                         EncrypterGateway encrypterGateway,
                                         NotificacionGateway notificacionGateway,
                                         TokenGateway tokenGateway) {
        return new UsuarioUseCase(usuarioGateway, encrypterGateway, notificacionGateway, tokenGateway);
    }
}
