package com.ecommerce.catalogo.infraestructure.message_broker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MensajeCola {
    private TipoNotificacion tipo;
    private NotificacionEmail email;
    private NotificacionSMS sms;
    
    public enum TipoNotificacion {
        EMAIL,
        SMS,
        AMBOS
    }
}
