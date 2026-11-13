package com.ecommerce.auth.infraestructura.mapper;

import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.infraestructura.driver_adapters.jpa_repository.UsuarioData;
import org.springframework.stereotype.Component;

@Component
public class MapperUsuario {

    public Usuario toUsuario(UsuarioData usuarioData) {
        if (usuarioData == null) {
            return null;
        }
        return new Usuario(
                usuarioData.getId(),
                usuarioData.getNombre(),
                usuarioData.getEmail(),
                usuarioData.getPassword(),
                usuarioData.getTelefono(),
                usuarioData.getRole(),
                usuarioData.getEdad());
    }

    public UsuarioData toUsuarioData(Usuario usuario) {
        return new UsuarioData(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getTelefono(),
                usuario.getRole(),
                usuario.getEdad());
    }
}
