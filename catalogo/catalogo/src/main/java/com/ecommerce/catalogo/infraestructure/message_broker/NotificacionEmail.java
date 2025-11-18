package com.ecommerce.catalogo.infraestructure.message_broker;

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
