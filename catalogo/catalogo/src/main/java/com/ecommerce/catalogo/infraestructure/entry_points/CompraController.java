package com.ecommerce.catalogo.infraestructure.entry_points;

import com.ecommerce.catalogo.domain.exception.CarritoVacioException;
import com.ecommerce.catalogo.domain.exception.UsuarioNoEncontradoException;
import com.ecommerce.catalogo.domain.model.Compra;
import com.ecommerce.catalogo.domain.usecase.CompraUseCase;
import com.ecommerce.catalogo.infraestructure.entry_points.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ecommerce/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraUseCase compraUseCase;

    @PostMapping("/realizar")
    public ResponseEntity<?> realizarCompra(@RequestParam Long usuarioId) {
        try {
            Compra compra = compraUseCase.realizarCompra(usuarioId);
            return ResponseEntity.ok(compra);
        } catch (UsuarioNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (CarritoVacioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al procesar la compra: " + e.getMessage()));
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerComprasPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<Compra> compras = compraUseCase.obtenerComprasPorUsuario(usuarioId);
            return ResponseEntity.ok(compras);
        } catch (UsuarioNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al obtener las compras: " + e.getMessage()));
        }
    }

    @GetMapping("/{compraId}")
    public ResponseEntity<?> obtenerCompraPorId(@PathVariable Long compraId) {
        try {
            Compra compra = compraUseCase.obtenerCompraPorId(compraId);
            if (compra == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Compra no encontrada con ID: " + compraId));
            }
            return ResponseEntity.ok(compra);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al obtener la compra: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenerTodasLasCompras() {
        try {
            List<Compra> compras = compraUseCase.obtenerTodasLasCompras();
            return ResponseEntity.ok(compras);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al obtener las compras: " + e.getMessage()));
        }
    }
}