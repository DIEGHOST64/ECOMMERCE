package com.ecommerce.auth.infraestructura.entry_points.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroUsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no es valido")
        @Size(max = 120, message = "El email no puede superar 120 caracteres")
        String email,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 6, max = 72, message = "La contrasena debe tener entre 6 y 72 caracteres")
        String password,

        @Size(max = 30, message = "El telefono no puede superar 30 caracteres")
        String telefono,

        @Min(value = 0, message = "La edad no puede ser negativa")
        @Max(value = 120, message = "La edad no es valida")
        Integer edad) {
}
