package com.ecommerce.catalogo.infraestructura.entry_points;

import com.ecommerce.catalogo.domain.exception.CantidadInvalidaException;
import com.ecommerce.catalogo.domain.exception.CarritoNoFoundException;
import com.ecommerce.catalogo.domain.exception.CarritoVacioException;
import com.ecommerce.catalogo.domain.exception.CompraNoEncontradaException;
import com.ecommerce.catalogo.domain.exception.DatosInvalidosException;
import com.ecommerce.catalogo.domain.exception.ItemCarritoNoEncontradoException;
import com.ecommerce.catalogo.domain.exception.ProductoNoEncontradoException;
import com.ecommerce.catalogo.domain.exception.ServicioUsuariosNoDisponibleException;
import com.ecommerce.catalogo.domain.exception.StockInsuficienteException;
import com.ecommerce.catalogo.domain.exception.UsuarioNoAutenticadoException;
import com.ecommerce.catalogo.domain.exception.UsuarioNoEncontradoException;
import com.ecommerce.catalogo.infraestructura.entry_points.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ProductoNoEncontradoException.class,
            CarritoNoFoundException.class,
            ItemCarritoNoEncontradoException.class,
            CompraNoEncontradaException.class,
            UsuarioNoEncontradoException.class})
    public ResponseEntity<ApiError> handleNotFound(RuntimeException ex,
                                                   HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler({StockInsuficienteException.class,
            CantidadInvalidaException.class,
            CarritoVacioException.class,
            DatosInvalidosException.class})
    public ResponseEntity<ApiError> handleBadRequest(RuntimeException ex,
                                                     HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(UsuarioNoAutenticadoException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UsuarioNoAutenticadoException ex,
                                                       HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(ServicioUsuariosNoDisponibleException.class)
    public ResponseEntity<ApiError> handleServicioUsuarios(
            ServicioUsuariosNoDisponibleException ex, HttpServletRequest request) {
        log.error("Servicio de usuarios no disponible", ex);
        return build(HttpStatus.SERVICE_UNAVAILABLE,
                "El servicio de usuarios no esta disponible", request);
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

    @ExceptionHandler({HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class})
    public ResponseEntity<ApiError> handleBadInput(Exception ex,
                                                   HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST,
                "La solicitud no tiene un formato valido", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleIntegrity(DataIntegrityViolationException ex,
                                                    HttpServletRequest request) {
        return build(HttpStatus.CONFLICT,
                "No fue posible guardar los datos solicitados", request);
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
