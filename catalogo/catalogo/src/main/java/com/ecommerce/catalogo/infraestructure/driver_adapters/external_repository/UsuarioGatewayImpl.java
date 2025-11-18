package com.ecommerce.catalogo.infraestructure.driver_adapters.external_repository;

import com.ecommerce.catalogo.domain.model.Usuario;
import com.ecommerce.catalogo.domain.model.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Component
public class UsuarioGatewayImpl implements UsuarioGateway {

    private final RestTemplate restTemplate;
    
    @Value("${auth.service.url}")
    private String authServiceUrl;

    @Override
    public boolean usuarioExiste(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            return false;
        }

        try {
                // Llamada al microservicio de autenticacion para verificar si el usuario existe
                String url = authServiceUrl + "/api/ecommerce/usuario/" + usuarioId + "/existe";
            ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return Boolean.TRUE.equals(response.getBody());
            }
            return false;
        } catch (Exception e) {
            // Si hay error en la comunicacion, consideramos que el usuario no existe
            System.err.println("Error al verificar usuario con ID " + usuarioId + ": " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public Usuario buscarPorId(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            return null;
        }

        try {
            String url = authServiceUrl + "/api/ecommerce/usuario/" + usuarioId;
            ResponseEntity<Usuario> response = restTemplate.getForEntity(url, Usuario.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error al obtener usuario con ID " + usuarioId + ": " + e.getMessage());
            return null;
        }
    }
}
