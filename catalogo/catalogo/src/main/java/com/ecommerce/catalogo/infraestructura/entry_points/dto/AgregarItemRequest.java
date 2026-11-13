package com.ecommerce.catalogo.infraestructura.entry_points.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AgregarItemRequest(
        @NotNull(message = "El producto es obligatorio")
        Long productoId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        @Max(value = 10, message = "No se pueden agregar mas de 10 unidades")
        Integer cantidad) {
}
