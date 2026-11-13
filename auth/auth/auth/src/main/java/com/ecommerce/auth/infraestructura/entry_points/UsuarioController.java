package com.ecommerce.auth.infraestructura.entry_points;

import com.ecommerce.auth.application.port.in.UsuarioService;
import com.ecommerce.auth.infraestructura.entry_points.dto.ActualizarUsuarioRequest;
import com.ecommerce.auth.infraestructura.entry_points.dto.LoginRequest;
import com.ecommerce.auth.infraestructura.entry_points.dto.LoginResponse;
import com.ecommerce.auth.infraestructura.entry_points.dto.RegistroUsuarioRequest;
import com.ecommerce.auth.infraestructura.entry_points.dto.UsuarioResponse;
import com.ecommerce.auth.infraestructura.entry_points.mapper.UsuarioDtoMapper;
import com.ecommerce.auth.infraestructura.security.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ecommerce/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioDtoMapper mapper;

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse save(@Valid @RequestBody RegistroUsuarioRequest request) {
        return mapper.toResponse(usuarioService.registrar(mapper.toDomain(request)));
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return mapper.toLoginResponse(
                usuarioService.login(request.email(), request.password()));
    }

    @GetMapping("/{id}")
    public UsuarioResponse findById(@PathVariable Long id,
                                    @AuthenticationPrincipal AuthenticatedUser actual) {
        asegurarPropietarioOAdmin(actual, id);
        return mapper.toResponse(usuarioService.buscarPorId(id));
    }

    /**
     * Endpoint interno usado por el servicio de catalogo para validar usuarios.
     * No expone datos personales, solo existencia.
     */
    @GetMapping("/{id}/existe")
    public boolean usuarioExiste(@PathVariable Long id) {
        return usuarioService.existePorId(id);
    }

    @PutMapping("/update")
    public UsuarioResponse update(@Valid @RequestBody ActualizarUsuarioRequest request,
                                  @AuthenticationPrincipal AuthenticatedUser actual) {
        asegurarPropietarioOAdmin(actual, request.id());
        return mapper.toResponse(usuarioService.actualizar(mapper.toDomain(request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUsuario(@PathVariable Long id) {
        usuarioService.eliminarPorId(id);
    }

    private void asegurarPropietarioOAdmin(AuthenticatedUser actual, Long id) {
        if (actual == null) {
            throw new AccessDeniedException("No autenticado");
        }
        boolean esAdmin = "ADMIN".equalsIgnoreCase(actual.role());
        if (!esAdmin && !actual.id().equals(id)) {
            throw new AccessDeniedException("Solo puedes acceder a tus propios datos");
        }
    }
}
