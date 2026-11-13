package com.ecommerce.catalogo.infraestructura.entry_points.dto;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

public record ApiError(
        String timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors) {

    public static ApiError of(HttpStatus status, String message, String path) {
        return new ApiError(Instant.now().toString(), status.value(),
                status.getReasonPhrase(), message, path, null);
    }

    public static ApiError of(HttpStatus status, String message, String path,
                              Map<String, String> fieldErrors) {
        return new ApiError(Instant.now().toString(), status.value(),
                status.getReasonPhrase(), message, path, fieldErrors);
    }
}
