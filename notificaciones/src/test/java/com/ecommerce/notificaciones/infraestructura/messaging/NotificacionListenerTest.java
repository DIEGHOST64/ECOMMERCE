package com.ecommerce.notificaciones.infraestructura.messaging;

import com.ecommerce.notificaciones.domain.exception.EnvioNotificacionException;
import com.ecommerce.notificaciones.domain.model.MensajeNotificacion;
import com.ecommerce.notificaciones.domain.model.NotificacionEmail;
import com.ecommerce.notificaciones.domain.port.in.ProcesarNotificacionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificacionListenerTest {

    @Mock
    private ProcesarNotificacionService procesarNotificacionService;

    @InjectMocks
    private NotificacionListener listener;

    @Test
    void delegaElMensajeEnElCasoDeUso() {
        MensajeNotificacion mensaje = MensajeNotificacion.builder()
                .tipo(MensajeNotificacion.TipoNotificacion.EMAIL)
                .email(NotificacionEmail.builder()
                        .destinatario("ana@example.com")
                        .asunto("Bienvenido")
                        .cuerpo("Hola")
                        .build())
                .build();

        listener.escuchar(mensaje);

        verify(procesarNotificacionService).procesar(mensaje);
    }

    @Test
    void propagaLaExcepcionParaQueRabbitReintente() {
        MensajeNotificacion mensaje = new MensajeNotificacion();
        doThrow(new EnvioNotificacionException("proveedor caido"))
                .when(procesarNotificacionService).procesar(mensaje);

        assertThrows(EnvioNotificacionException.class, () -> listener.escuchar(mensaje));
    }
}
