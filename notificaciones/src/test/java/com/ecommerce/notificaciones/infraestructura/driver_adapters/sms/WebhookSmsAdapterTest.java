package com.ecommerce.notificaciones.infraestructura.driver_adapters.sms;

import com.ecommerce.notificaciones.domain.exception.EnvioNotificacionException;
import com.ecommerce.notificaciones.domain.model.NotificacionSMS;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WebhookSmsAdapterTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private HttpServer server;

    @AfterEach
    void detenerServidor() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void enviaElSmsAlWebhookConTokenBearer() throws Exception {
        AtomicReference<String> cuerpo = new AtomicReference<>();
        AtomicReference<String> autorizacion = new AtomicReference<>();
        String url = iniciarServidor(202, cuerpo, autorizacion);

        WebhookSmsAdapter adapter = new WebhookSmsAdapter(
                RestClient.builder(), url, "secreto", 1_000, 2_000);

        adapter.enviarSms(new NotificacionSMS("+573001234567", "Hola Ana"));

        JsonNode json = objectMapper.readTree(cuerpo.get());
        assertEquals("+573001234567", json.get("to").asText());
        assertEquals("Hola Ana", json.get("message").asText());
        assertEquals("Bearer secreto", autorizacion.get());
    }

    @Test
    void fallaSiElWebhookRespondeError() throws Exception {
        String url = iniciarServidor(500, new AtomicReference<>(), new AtomicReference<>());
        WebhookSmsAdapter adapter = new WebhookSmsAdapter(
                RestClient.builder(), url, "", 1_000, 2_000);

        assertThrows(EnvioNotificacionException.class, () -> adapter.enviarSms(
                new NotificacionSMS("+573001234567", "Hola")));
    }

    @Test
    void exigeUrlCuandoSeUsaElProveedorWebhook() {
        assertThrows(IllegalStateException.class, () -> new WebhookSmsAdapter(
                RestClient.builder(), " ", null, 1_000, 2_000));
    }

    private String iniciarServidor(int status,
                                   AtomicReference<String> cuerpo,
                                   AtomicReference<String> autorizacion) throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/send", exchange -> {
            cuerpo.set(new String(exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8));
            autorizacion.set(exchange.getRequestHeaders().getFirst("Authorization"));
            byte[] respuesta = "{\"ok\":true}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, respuesta.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(respuesta);
            }
        });
        server.start();
        return "http://localhost:" + server.getAddress().getPort() + "/send";
    }
}
