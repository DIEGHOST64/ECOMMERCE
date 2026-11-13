package com.ecommerce.catalogo.infraestructura.config;

import com.ecommerce.catalogo.application.port.in.CarritoService;
import com.ecommerce.catalogo.application.port.in.CompraService;
import com.ecommerce.catalogo.application.port.in.ProductoService;
import com.ecommerce.catalogo.application.usecase.CarritoUseCase;
import com.ecommerce.catalogo.application.usecase.CompraUseCase;
import com.ecommerce.catalogo.application.usecase.ProductoUseCase;
import com.ecommerce.catalogo.domain.port.out.CarritoGateway;
import com.ecommerce.catalogo.domain.port.out.CompraGateway;
import com.ecommerce.catalogo.domain.port.out.NotificacionGateway;
import com.ecommerce.catalogo.domain.port.out.ProductoGateway;
import com.ecommerce.catalogo.domain.port.out.UsuarioGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public ProductoService productoService(ProductoGateway productoGateway) {
        return new ProductoUseCase(productoGateway);
    }

    @Bean
    public CarritoService carritoService(CarritoGateway carritoGateway,
                                         ProductoGateway productoGateway,
                                         UsuarioGateway usuarioGateway) {
        return new CarritoUseCase(carritoGateway, productoGateway, usuarioGateway);
    }

    @Bean
    public CompraService compraService(CompraGateway compraGateway,
                                       CarritoGateway carritoGateway,
                                       ProductoGateway productoGateway,
                                       UsuarioGateway usuarioGateway,
                                       NotificacionGateway notificacionGateway) {
        return new CompraUseCase(compraGateway, carritoGateway, productoGateway,
                usuarioGateway, notificacionGateway);
    }
}
