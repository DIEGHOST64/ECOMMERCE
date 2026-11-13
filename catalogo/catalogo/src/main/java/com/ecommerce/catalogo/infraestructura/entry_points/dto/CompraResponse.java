package com.ecommerce.catalogo.infraestructura.entry_points.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CompraResponse(
        Long id,
        Long usuarioId,
        LocalDateTime fechaCompra,
        List<ItemCarritoResponse> items,
        BigDecimal total,
        String estado) {
}
