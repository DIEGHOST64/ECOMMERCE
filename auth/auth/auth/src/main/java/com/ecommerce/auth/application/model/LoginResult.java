package com.ecommerce.auth.application.model;

import com.ecommerce.auth.domain.model.Usuario;

/**
 * Resultado del caso de uso de login. El token se genera a traves del puerto
 * {@code TokenGateway}, por lo que la capa de aplicacion no conoce JWT.
 */
public record LoginResult(Usuario usuario, String token, long expiraEnSegundos) {
}
