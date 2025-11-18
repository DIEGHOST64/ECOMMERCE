package com.ecommerce.catalogo.domain.model.gateway;

import com.ecommerce.catalogo.domain.model.Usuario;

public interface UsuarioGateway {

    boolean usuarioExiste(Long usuarioId);
    
    Usuario buscarPorId(Long usuarioId);
}
