package com.ecommerce.catalogo.infraestructure.entry_points;

import com.ecommerce.catalogo.domain.exception.*;
import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.ItemCarrito;
import com.ecommerce.catalogo.domain.usecase.CarritoUseCase;
import com.ecommerce.catalogo.infraestructure.entry_points.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ecommerce/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoUseCase carritoUseCase;

    @PostMapping("/agregar")
    public ResponseEntity<?> agregarItem(@RequestParam Long usuarioId, @RequestBody ItemCarrito item) {
        try {
            return ResponseEntity.ok(carritoUseCase.agregarProductoAlCarrito(usuarioId, item));
        } catch (UsuarioNoEncontradoException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Usuario no encontrado con ID: " + usuarioId));
        } catch (StockInsuficienteException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (CantidadInvalidaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> verCarrito(@RequestParam Long usuarioId) {
        try {
            return ResponseEntity.ok(carritoUseCase.obtenerCarrito(usuarioId));
        } catch (UsuarioNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor: " + e.getMessage()));
        }
    }

    @DeleteMapping("/vaciar")
    public ResponseEntity<?> vaciar(@RequestParam Long usuarioId) {
        try {
            carritoUseCase.vaciarCarrito(usuarioId);
            return ResponseEntity.ok(new ErrorResponse("Carrito vaciado correctamente"));
        } catch (CarritoVacioException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (UsuarioNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor: " + e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar/{productoId}")
    public ResponseEntity<?> eliminarItemDelCarrito(
            @RequestParam Long usuarioId,
            @PathVariable Long productoId
    ) {
        try {
            Carrito carritoActualizado = carritoUseCase.eliminarProducto(usuarioId, productoId);
            return ResponseEntity.ok(carritoActualizado);
        } catch (UsuarioNoEncontradoException | CarritoVacioException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor: " + e.getMessage()));
        }
    }

    @PutMapping("/actualizar/{productoId}")
    public ResponseEntity<?> actualizarCantidad(
            @RequestParam Long usuarioId,
            @PathVariable Long productoId,
            @RequestParam Integer cantidad
    ) {
        try {
            Carrito carritoActualizado = carritoUseCase.actualizarCantidadProducto(usuarioId, productoId, cantidad);
            return ResponseEntity.ok(carritoActualizado);
        } catch (UsuarioNoEncontradoException | CarritoVacioException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (StockInsuficienteException | CantidadInvalidaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor: " + e.getMessage()));
        }
    }
}
