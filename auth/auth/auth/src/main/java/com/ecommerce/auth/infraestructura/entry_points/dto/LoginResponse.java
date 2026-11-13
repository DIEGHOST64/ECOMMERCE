package com.ecommerce.auth.infraestructura.entry_points.dto;

/**
 * Respuesta de login compatible con el frontend existente: mantiene los
 * campos planos (id, email, nombre, role) y agrega el token JWT.
 */
public record LoginResponse(
        Long id,
        String email,
        String nombre,
        String role,
        String token,
        String tokenType,
        long expiraEnSegundos,
        String mensaje) {
}
