package com.ecommerce.auth.infraestructura.entry_points.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Datos actualizables por el propio usuario o por un administrador.
 * El rol nunca se acepta desde el cliente.
 */
public record ActualizarUsuarioRequest(
        @NotNull(message = "El id es obligatorio")
        Long id,

        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @Email(message = "El email no es valido")
        @Size(max = 120, message = "El email no puede superar 120 caracteres")
        String email,

        @Size(max = 72, message = "La contrasena no puede superar 72 caracteres")
        String password,

        @Size(max = 30, message = "El telefono no puede superar 30 caracteres")
        String telefono,

        @Min(value = 0, message = "La edad no puede ser negativa")
        @Max(value = 120, message = "La edad no es valida")
        Integer edad) {
}
