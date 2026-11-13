package com.ecommerce.auth.domain.port.out;

import com.ecommerce.auth.domain.model.Usuario;

/**
 * Puerto de salida para la persistencia de usuarios.
 */
public interface UsuarioGateway {

    Usuario guardar(Usuario usuario);

    void eliminarPorID(Long id);

    /** Retorna {@code null} cuando el usuario no existe. */
    Usuario buscarPorID(Long id);

    Usuario actualizarUsario(Usuario usuario);

    /** Retorna {@code null} cuando no existe un usuario con ese email. */
    Usuario buscarPorEmail(String email);
}
