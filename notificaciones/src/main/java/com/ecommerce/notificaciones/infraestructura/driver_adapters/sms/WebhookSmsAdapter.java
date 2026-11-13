package com.ecommerce.notificaciones.infraestructura.driver_adapters.sms;

import com.ecommerce.notificaciones.domain.exception.EnvioNotificacionException;
import com.ecommerce.notificaciones.domain.model.NotificacionSMS;
import com.ecommerce.notificaciones.domain.port.out.SmsGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Adaptador SMS generico mediante webhook HTTP (Jasmin, Kannel, gateway
 * Android, etc.). Se activa con {@code SMS_PROVIDER=webhook}.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.sms.provider", havingValue = "webhook")
public class WebhookSmsAdapter implements SmsGateway {

    private final RestClient restClient;
    private final String url;
    private final String token;

    public WebhookSmsAdapter(RestClient.Builder builder,
                             @Value("${app.sms.webhook.url:}") String url,
                             @Value("${app.sms.webhook.token:}") String token,
                             @Value("${app.sms.webhook.connect-timeout-ms:3000}") int connectTimeoutMs,
                             @Value("${app.sms.webhook.read-timeout-ms:5000}") int readTimeoutMs) {
        if (url == null || url.isBlank()) {
            throw new IllegalStateException(
                    "SMS_WEBHOOK_URL es obligatorio cuando SMS_PROVIDER=webhook");
        }
        this.url = url;
        this.token = token;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeoutMs);
        factory.setReadTimeout(readTimeoutMs);
        this.restClient = builder.requestFactory(factory).build();
    }

    @Override
    public void enviarSms(NotificacionSMS notificacion) {
        try {
            var request = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> {
                        if (token != null && !token.isBlank()) {
                            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                        }
                    })
                    .body(new SmsWebhookRequest(
                            notificacion.getNumeroTelefono(),
                            notificacion.getMensaje()));

            request.retrieve().toBodilessEntity();
            log.info("SMS enviado via webhook a {}", notificacion.getNumeroTelefono());
        } catch (Exception e) {
            throw new EnvioNotificacionException(
                    "No se pudo enviar el SMS a " + notificacion.getNumeroTelefono(), e);
        }
    }

    public record SmsWebhookRequest(String to, String message) {
    }
}
