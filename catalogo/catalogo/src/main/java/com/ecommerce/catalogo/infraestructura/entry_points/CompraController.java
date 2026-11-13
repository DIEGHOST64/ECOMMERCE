package com.ecommerce.catalogo.infraestructura.entry_points;

import com.ecommerce.catalogo.application.port.in.CompraService;
import com.ecommerce.catalogo.domain.model.Compra;
import com.ecommerce.catalogo.infraestructura.entry_points.dto.CompraResponse;
import com.ecommerce.catalogo.infraestructura.entry_points.mapper.TiendaDtoMapper;
import com.ecommerce.catalogo.infraestructura.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ecommerce/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;
    private final TiendaDtoMapper mapper;

    @PostMapping("/realizar")
    @ResponseStatus(HttpStatus.CREATED)
    public CompraResponse realizarCompra(
            @AuthenticationPrincipal AuthenticatedUser usuario) {
        return mapper.toResponse(compraService.realizarCompra(usuarioId(usuario)));
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<CompraResponse> obtenerComprasPorUsuario(
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @PathVariable Long usuarioId) {
        asegurarPropietarioOAdmin(usuario, usuarioId);
        return compraService.obtenerComprasPorUsuario(usuarioId)
                .stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{compraId}")
    public CompraResponse obtenerCompraPorId(
            @AuthenticationPrincipal AuthenticatedUser usuario,
            @PathVariable Long compraId) {
        Compra compra = compraService.obtenerCompraPorId(compraId);
        asegurarPropietarioOAdmin(usuario, compra.getUsuarioId());
        return mapper.toResponse(compra);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<CompraResponse> obtenerTodasLasCompras() {
        return compraService.obtenerTodasLasCompras()
                .stream().map(mapper::toResponse).toList();
    }

    private static Long usuarioId(AuthenticatedUser usuario) {
        if (usuario == null) {
            throw new AccessDeniedException("No autenticado");
        }
        return usuario.id();
    }

    private static void asegurarPropietarioOAdmin(AuthenticatedUser usuario, Long propietarioId) {
        if (usuario == null) {
            throw new AccessDeniedException("No autenticado");
        }
        boolean esAdmin = "ADMIN".equalsIgnoreCase(usuario.role());
        if (!esAdmin && !usuario.id().equals(propietarioId)) {
            throw new AccessDeniedException("Solo puedes acceder a tus propias compras");
        }
    }
}
