package com.ecommerce.catalogo.infraestructura.entry_points;

import com.ecommerce.catalogo.application.port.in.ProductoService;
import com.ecommerce.catalogo.infraestructura.entry_points.dto.ProductoRequest;
import com.ecommerce.catalogo.infraestructura.entry_points.dto.ProductoResponse;
import com.ecommerce.catalogo.infraestructura.entry_points.mapper.TiendaDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ecommerce/producto")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final TiendaDtoMapper mapper;

    @GetMapping
    public List<ProductoResponse> obtenerTodos() {
        return productoService.obtenerTodos().stream().map(mapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ProductoResponse obtenerPorId(@PathVariable Long id) {
        return mapper.toResponse(productoService.obtenerProducto(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ProductoResponse guardar(@Valid @RequestBody ProductoRequest request) {
        return mapper.toResponse(
                productoService.guardarProducto(mapper.toDomain(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductoResponse actualizar(@PathVariable Long id,
                                       @Valid @RequestBody ProductoRequest request) {
        var producto = mapper.toDomain(request);
        producto.setId(id);
        return mapper.toResponse(productoService.actualizarProducto(producto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable Long id) {
        productoService.eliminarProducto(id);
    }
}
