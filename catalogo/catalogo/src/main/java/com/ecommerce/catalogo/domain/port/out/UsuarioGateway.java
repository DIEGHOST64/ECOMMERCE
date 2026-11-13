package com.ecommerce.catalogo.domain.port.out;

import com.ecommerce.catalogo.domain.model.Usuario;

public interface UsuarioGateway {

    /** Retorna {@code null} cuando el usuario no existe. */
    Usuario buscarPorId(Long usuarioId);
}
