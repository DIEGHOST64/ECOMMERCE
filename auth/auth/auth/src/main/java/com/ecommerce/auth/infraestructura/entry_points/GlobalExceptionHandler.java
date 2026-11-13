package com.ecommerce.auth.infraestructura.entry_points;

import com.ecommerce.auth.domain.exception.CredencialesInvalidasException;
import com.ecommerce.auth.domain.exception.DatosInvalidosException;
import com.ecommerce.auth.domain.exception.EmailEnUsoException;
import com.ecommerce.auth.domain.exception.UsuarioNoEncontradoException;
import com.ecommerce.auth.infraestructura.entry_points.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ApiError> handleNotFound(UsuarioNoEncontradoException ex,
                                                   HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(EmailEnUsoException.class)
    public ResponseEntity<ApiError> handleEmailEnUso(EmailEnUsoException ex,
                                                     HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ApiError> handleCredenciales(CredencialesInvalidasException ex,
                                                       HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<ApiError> handleDatosInvalidos(DatosInvalidosException ex,
                                                         HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex,
                                                       HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN,
                ex.getMessage() == null ? "Acceso denegado" : ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                     HttpServletRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.putIfAbsent(error.getField(), error.getDefaultMessage()));

        ApiError body = ApiError.of(HttpStatus.BAD_REQUEST,
                "Hay campos invalidos en la solicitud",
                request.getRequestURI(),
                errores);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex,
                                                     HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud no es JSON valido", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleIntegrity(DataIntegrityViolationException ex,
                                                    HttpServletRequest request) {
        return build(HttpStatus.CONFLICT,
                "No fue posible guardar los datos: puede que el email ya exista", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado en {} {}", request.getMethod(), request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrio un error inesperado", request);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message,
                                           HttpServletRequest request) {
        return ResponseEntity.status(status)
                .body(ApiError.of(status, message, request.getRequestURI()));
    }
}
