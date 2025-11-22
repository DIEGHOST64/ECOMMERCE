package com.ecommerce.notificaciones.domain.usecase;

import com.ecommerce.notificaciones.domain.gateway.EmailGateway;
import com.ecommerce.notificaciones.domain.gateway.SMSGateway;
import com.ecommerce.notificaciones.domain.model.MensajeCola;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProcesarNotificacionUseCase {
    
    private final EmailGateway emailGateway;
    private final SMSGateway smsGateway;
    
    public void procesar(MensajeCola mensaje) {
        try {
            switch (mensaje.getTipo()) {
                case EMAIL:
                    if (mensaje.getEmail() != null) {
                        try {
                            emailGateway.enviarEmail(mensaje.getEmail());
                        } catch (Exception e) {
                            
                        }
                    }
                    break;
                    
                case SMS:
                    if (mensaje.getSms() != null) {
                        try {
                            smsGateway.enviarSMS(mensaje.getSms());
                        } catch (Exception e) {
                            
                        }
                    }
                    break;
                    
                case AMBOS:
                    if (mensaje.getEmail() != null) {
                        try {
                            emailGateway.enviarEmail(mensaje.getEmail());
                        } catch (Exception e) {
                            
                        }
                    }
                    if (mensaje.getSms() != null) {
                        try {
                            smsGateway.enviarSMS(mensaje.getSms());
                        } catch (Exception e) {
                            
                        }
                    }
                    break;
            }
        } catch (Exception e) {  
             
        }
    }
}
