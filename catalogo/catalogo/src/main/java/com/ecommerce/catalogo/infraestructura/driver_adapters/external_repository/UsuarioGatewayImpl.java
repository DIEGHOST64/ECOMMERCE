package com.ecommerce.catalogo.infraestructura.driver_adapters.external_repository;

import com.ecommerce.catalogo.domain.exception.ServicioUsuariosNoDisponibleException;
import com.ecommerce.catalogo.domain.exception.UsuarioNoAutenticadoException;
import com.ecommerce.catalogo.domain.model.Usuario;
import com.ecommerce.catalogo.domain.port.out.UsuarioGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Cliente del servicio auth. Reenvia el JWT del usuario para que auth
 * aplique sus propias reglas de autorizacion (self/admin).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UsuarioGatewayImpl implements UsuarioGateway {

    private final RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    @Override
    public Usuario buscarPorId(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            return null;
        }

        String url = authServiceUrl + "/api/ecommerce/usuario/" + usuarioId;
        try {
            ResponseEntity<Usuario> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(authorizationHeaders()), Usuario.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            throw new UsuarioNoAutenticadoException("El token del usuario no es valido");
        } catch (RestClientException e) {
            log.error("No se pudo consultar el usuario {} en auth: {}", usuarioId, e.getMessage());
            throw new ServicioUsuariosNoDisponibleException(
                    "No se pudo validar el usuario en el servicio de autenticacion", e);
        }
    }

    private static HttpHeaders authorizationHeaders() {
        HttpHeaders headers = new HttpHeaders();
        var attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            String authorization = servletAttributes.getRequest()
                    .getHeader(HttpHeaders.AUTHORIZATION);
            if (authorization != null && !authorization.isBlank()) {
                headers.set(HttpHeaders.AUTHORIZATION, authorization);
            }
        }
        return headers;
    }
}
