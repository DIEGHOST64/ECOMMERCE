package com.ecommerce.catalogo.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Compra {
    private Long id;
    private Long usuarioId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCompra;
    
    private List<ItemCarrito> items;
    private Double total;
    private String estado; // COMPLETADA, PENDIENTE, CANCELADA
}
