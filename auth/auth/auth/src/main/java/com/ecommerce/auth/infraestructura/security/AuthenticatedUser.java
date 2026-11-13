package com.ecommerce.auth.infraestructura.security;

/**
 * Principal autenticado a partir del JWT.
 */
public record AuthenticatedUser(Long id, String email, String nombre, String role) {
}
