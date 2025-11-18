package com.ecommerce.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionEmail {
    private String destinatario;
    private String asunto;
    private String cuerpo;
    private boolean html;
}
