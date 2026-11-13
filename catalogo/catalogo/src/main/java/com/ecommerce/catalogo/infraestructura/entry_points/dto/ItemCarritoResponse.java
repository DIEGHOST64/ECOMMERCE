package com.ecommerce.catalogo.infraestructura.entry_points.dto;

import java.math.BigDecimal;

public record ItemCarritoResponse(
        Long id,
        Long productoId,
        String nombreProducto,
        BigDecimal precioUnitario,
        Integer cantidad,
        BigDecimal subtotal,
        String imagenUrl) {
}
