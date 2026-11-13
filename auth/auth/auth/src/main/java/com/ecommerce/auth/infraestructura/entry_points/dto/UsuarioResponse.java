package com.ecommerce.auth.infraestructura.entry_points.dto;

public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        String telefono,
        String role,
        Integer edad) {
}
