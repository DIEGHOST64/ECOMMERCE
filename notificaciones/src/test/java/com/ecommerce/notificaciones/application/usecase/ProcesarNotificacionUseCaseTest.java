package com.ecommerce.notificaciones.application.usecase;

import com.ecommerce.notificaciones.domain.exception.DatosInvalidosException;
import com.ecommerce.notificaciones.domain.exception.EnvioNotificacionException;
import com.ecommerce.notificaciones.domain.model.MensajeNotificacion;
import com.ecommerce.notificaciones.domain.model.NotificacionEmail;
import com.ecommerce.notificaciones.domain.model.NotificacionSMS;
import com.ecommerce.notificaciones.domain.port.out.EmailGateway;
import com.ecommerce.notificaciones.domain.port.out.SmsGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcesarNotificacionUseCaseTest {

    @Mock
    private EmailGateway emailGateway;

    @Mock
    private SmsGateway smsGateway;

    @InjectMocks
    private ProcesarNotificacionUseCase useCase;

    @Test
    void enviaSoloEmailCuandoElTipoEsEmail() {
        useCase.procesar(mensaje(MensajeNotificacion.TipoNotificacion.EMAIL,
                email(), null));

        verify(emailGateway).enviarEmail(any(NotificacionEmail.class));
        verify(smsGateway, never()).enviarSms(any());
    }

    @Test
    void enviaSoloSmsCuandoElTipoEsSms() {
        useCase.procesar(mensaje(MensajeNotificacion.TipoNotificacion.SMS,
                null, sms()));

        verify(smsGateway).enviarSms(any(NotificacionSMS.class));
        verify(emailGateway, never()).enviarEmail(any());
    }

    @Test
    void enviaAmbosCanalesCuandoElTipoEsAmbos() {
        useCase.procesar(mensaje(MensajeNotificacion.TipoNotificacion.AMBOS,
                email(), sms()));

        verify(emailGateway).enviarEmail(any(NotificacionEmail.class));
        verify(smsGateway).enviarSms(any(NotificacionSMS.class));
    }

    @Test
    void rechazaMensajeSinTipo() {
        MensajeNotificacion mensaje = new MensajeNotificacion();
        mensaje.setEmail(email());

        assertThrows(DatosInvalidosException.class, () -> useCase.procesar(mensaje));
    }

    @Test
    void rechazaEmailSinDestinatario() {
        NotificacionEmail email = email();
        email.setDestinatario(" ");

        assertThrows(DatosInvalidosException.class, () -> useCase.procesar(
                mensaje(MensajeNotificacion.TipoNotificacion.EMAIL, email, null)));
        verify(emailGateway, never()).enviarEmail(any());
    }

    @Test
    void rechazaSmsSinNumero() {
        NotificacionSMS sms = sms();
        sms.setNumeroTelefono(null);

        assertThrows(DatosInvalidosException.class, () -> useCase.procesar(
                mensaje(MensajeNotificacion.TipoNotificacion.SMS, null, sms)));
        verify(smsGateway, never()).enviarSms(any());
    }

    @Test
    void propagaFalloDeEmailParaQueRabbitReintente() {
        doThrow(new EnvioNotificacionException("SMTP caido"))
                .when(emailGateway).enviarEmail(any());

        assertThrows(EnvioNotificacionException.class, () -> useCase.procesar(
                mensaje(MensajeNotificacion.TipoNotificacion.EMAIL, email(), null)));
    }

    @Test
    void enAmbosIntentaSmsAunqueFalleElEmail() {
        doThrow(new EnvioNotificacionException("SMTP caido"))
                .when(emailGateway).enviarEmail(any());

        assertThrows(EnvioNotificacionException.class, () -> useCase.procesar(
                mensaje(MensajeNotificacion.TipoNotificacion.AMBOS, email(), sms())));

        verify(smsGateway).enviarSms(any(NotificacionSMS.class));
    }

    private static MensajeNotificacion mensaje(MensajeNotificacion.TipoNotificacion tipo,
                                               NotificacionEmail email,
                                               NotificacionSMS sms) {
        return MensajeNotificacion.builder()
                .tipo(tipo)
                .email(email)
                .sms(sms)
                .build();
    }

    private static NotificacionEmail email() {
        return NotificacionEmail.builder()
                .destinatario("ana@example.com")
                .asunto("Bienvenido")
                .cuerpo("<p>Hola</p>")
                .html(true)
                .build();
    }

    private static NotificacionSMS sms() {
        return NotificacionSMS.builder()
                .numeroTelefono("+573001234567")
                .mensaje("Hola")
                .build();
    }
}
