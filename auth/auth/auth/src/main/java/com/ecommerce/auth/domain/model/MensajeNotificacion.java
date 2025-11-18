package com.ecommerce.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MensajeNotificacion {
    private TipoNotificacion tipo;
    private NotificacionEmail email;
    private NotificacionSMS sms;
    
    public enum TipoNotificacion {
        EMAIL,
        SMS,
        AMBOS
    }
}
