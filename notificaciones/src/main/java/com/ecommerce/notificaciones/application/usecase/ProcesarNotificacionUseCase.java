package com.ecommerce.notificaciones.application.usecase;

import com.ecommerce.notificaciones.domain.exception.DatosInvalidosException;
import com.ecommerce.notificaciones.domain.exception.EnvioNotificacionException;
import com.ecommerce.notificaciones.domain.model.MensajeNotificacion;
import com.ecommerce.notificaciones.domain.model.NotificacionEmail;
import com.ecommerce.notificaciones.domain.model.NotificacionSMS;
import com.ecommerce.notificaciones.domain.port.in.ProcesarNotificacionService;
import com.ecommerce.notificaciones.domain.port.out.EmailGateway;
import com.ecommerce.notificaciones.domain.port.out.SmsGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Procesa notificaciones y propaga los fallos de envio.
 *
 * <p>Es importante no silenciar las excepciones: RabbitMQ necesita saber que
 * el mensaje no pudo procesarse para reintentarlo y, cuando se agoten los
 * intentos, derivarlo a la cola de mensajes muertos (DLQ).</p>
 */
@Slf4j
@RequiredArgsConstructor
public class ProcesarNotificacionUseCase implements ProcesarNotificacionService {

    private final EmailGateway emailGateway;
    private final SmsGateway smsGateway;

    @Override
    public void procesar(MensajeNotificacion mensaje) {
        if (mensaje == null || mensaje.getTipo() == null) {
            throw new DatosInvalidosException("La notificacion debe indicar su tipo");
        }

        List<String> errores = new ArrayList<>();

        // La validacion ocurre fuera del try: un mensaje mal formado no debe
        // reintentarse, mientras que un fallo del proveedor si.
        NotificacionEmail email = requiereEmail(mensaje.getTipo())
                ? validarEmail(mensaje.getEmail())
                : null;
        NotificacionSMS sms = requiereSms(mensaje.getTipo())
                ? validarSms(mensaje.getSms())
                : null;

        if (email != null) {
            try {
                enviarEmail(email);
            } catch (RuntimeException e) {
                log.error("Fallo el envio de email: {}", e.getMessage());
                errores.add("email: " + e.getMessage());
            }
        }

        if (sms != null) {
            try {
                enviarSms(sms);
            } catch (RuntimeException e) {
                log.error("Fallo el envio de SMS: {}", e.getMessage());
                errores.add("sms: " + e.getMessage());
            }
        }

        if (!errores.isEmpty()) {
            throw new EnvioNotificacionException(
                    "No se pudieron enviar todas las notificaciones: "
                            + String.join("; ", errores));
        }
    }

    private void enviarEmail(NotificacionEmail email) {
        emailGateway.enviarEmail(email);
        log.info("Email enviado a {}", email.getDestinatario());
    }

    private void enviarSms(NotificacionSMS sms) {
        smsGateway.enviarSms(sms);
        log.info("SMS enviado a {}", sms.getNumeroTelefono());
    }

    private static boolean requiereEmail(MensajeNotificacion.TipoNotificacion tipo) {
        return tipo == MensajeNotificacion.TipoNotificacion.EMAIL
                || tipo == MensajeNotificacion.TipoNotificacion.AMBOS;
    }

    private static boolean requiereSms(MensajeNotificacion.TipoNotificacion tipo) {
        return tipo == MensajeNotificacion.TipoNotificacion.SMS
                || tipo == MensajeNotificacion.TipoNotificacion.AMBOS;
    }

    private static NotificacionEmail validarEmail(NotificacionEmail email) {
        if (email == null) {
            throw new DatosInvalidosException("El mensaje no incluye datos de email");
        }
        if (esBlanco(email.getDestinatario())) {
            throw new DatosInvalidosException("El destinatario del email es obligatorio");
        }
        if (esBlanco(email.getAsunto())) {
            throw new DatosInvalidosException("El asunto del email es obligatorio");
        }
        if (esBlanco(email.getCuerpo())) {
            throw new DatosInvalidosException("El cuerpo del email es obligatorio");
        }
        return email;
    }

    private static NotificacionSMS validarSms(NotificacionSMS sms) {
        if (sms == null) {
            throw new DatosInvalidosException("El mensaje no incluye datos de SMS");
        }
        if (esBlanco(sms.getNumeroTelefono())) {
            throw new DatosInvalidosException("El numero de telefono es obligatorio");
        }
        if (esBlanco(sms.getMensaje())) {
            throw new DatosInvalidosException("El texto del SMS es obligatorio");
        }
        return sms;
    }

    private static boolean esBlanco(String valor) {
        return valor == null || valor.isBlank();
    }
}
