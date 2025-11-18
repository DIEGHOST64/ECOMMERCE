package com.ecommerce.catalogo.infraestructure.entry_points.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String mensaje;
}