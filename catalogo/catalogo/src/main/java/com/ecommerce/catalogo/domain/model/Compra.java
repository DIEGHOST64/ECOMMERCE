package com.ecommerce.catalogo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Compra {
    private Long id;
    private Long usuarioId;
    private LocalDateTime fechaCompra;
    private List<ItemCarrito> items;
    private BigDecimal total;
    private String estado; // COMPLETADA, PENDIENTE, CANCELADA
}
