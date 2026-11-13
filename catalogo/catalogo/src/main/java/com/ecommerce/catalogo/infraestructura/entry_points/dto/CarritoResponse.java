package com.ecommerce.catalogo.infraestructura.entry_points.dto;

import java.math.BigDecimal;
import java.util.List;

public record CarritoResponse(
        Long id,
        Long usuarioId,
        List<ItemCarritoResponse> items,
        BigDecimal precioTotal) {
}
