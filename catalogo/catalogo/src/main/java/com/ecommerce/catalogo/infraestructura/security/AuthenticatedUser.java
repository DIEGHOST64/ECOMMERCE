package com.ecommerce.catalogo.infraestructura.security;

public record AuthenticatedUser(Long id, String email, String nombre, String role) {
}
