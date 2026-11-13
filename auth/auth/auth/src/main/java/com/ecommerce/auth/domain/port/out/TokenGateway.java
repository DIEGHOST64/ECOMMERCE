package com.ecommerce.auth.domain.port.out;

import com.ecommerce.auth.domain.model.Usuario;

/**
 * Puerto de salida para la generacion de tokens de acceso.
 */
public interface TokenGateway {

    String generarToken(Usuario usuario);

    long expiracionEnSegundos();
}
