package com.ecommerce.auth.application.port.in;

import com.ecommerce.auth.application.model.LoginResult;
import com.ecommerce.auth.domain.model.Usuario;

/**
 * Puerto de entrada con los casos de uso de usuarios.
 */
public interface UsuarioService {

    Usuario registrar(Usuario usuario);

    LoginResult login(String email, String password);

    Usuario buscarPorId(Long id);

    boolean existePorId(Long id);

    Usuario actualizar(Usuario usuario);

    void eliminarPorId(Long id);
}
