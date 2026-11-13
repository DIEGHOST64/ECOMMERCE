package com.ecommerce.auth.infraestructura.entry_points.mapper;

import com.ecommerce.auth.application.model.LoginResult;
import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.infraestructura.entry_points.dto.ActualizarUsuarioRequest;
import com.ecommerce.auth.infraestructura.entry_points.dto.LoginResponse;
import com.ecommerce.auth.infraestructura.entry_points.dto.RegistroUsuarioRequest;
import com.ecommerce.auth.infraestructura.entry_points.dto.UsuarioResponse;
import org.springframework.stereotype.Component;

@Component
public class UsuarioDtoMapper {

    public Usuario toDomain(RegistroUsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        usuario.setPassword(request.password());
        usuario.setTelefono(request.telefono());
        usuario.setEdad(request.edad());
        return usuario;
    }

    public Usuario toDomain(ActualizarUsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setId(request.id());
        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        usuario.setPassword(request.password());
        usuario.setTelefono(request.telefono());
        usuario.setEdad(request.edad());
        return usuario;
    }

    public UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getRole(),
                usuario.getEdad());
    }

    public LoginResponse toLoginResponse(LoginResult result) {
        Usuario usuario = result.usuario();
        return new LoginResponse(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getRole(),
                result.token(),
                "Bearer",
                result.expiraEnSegundos(),
                "Login exitoso");
    }
}
