package com.ecommerce.catalogo.application.usecase;

import com.ecommerce.catalogo.domain.exception.DatosInvalidosException;
import com.ecommerce.catalogo.domain.exception.ProductoNoEncontradoException;
import com.ecommerce.catalogo.domain.exception.StockInsuficienteException;
import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.domain.port.out.ProductoGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoUseCaseTest {

    @Mock
    private ProductoGateway productoGateway;

    @InjectMocks
    private ProductoUseCase useCase;

    @Test
    void descontarStockFallaCuandoElGatewayNoActualizaFilas() {
        when(productoGateway.descontarStock(10L, 2)).thenReturn(false);

        assertThrows(StockInsuficienteException.class,
                () -> useCase.descontarStock(10L, 2));
    }

    @Test
    void noPermiteGuardarProductoConStockNegativo() {
        Producto producto = new Producto();
        producto.setNombre("Teclado");
        producto.setPrecio(new BigDecimal("10.00"));
        producto.setStock(-1);

        assertThrows(DatosInvalidosException.class,
                () -> useCase.guardarProducto(producto));
    }

    @Test
    void obtenerProductoInexistenteFalla() {
        when(productoGateway.buscarPorId(99L)).thenReturn(null);

        assertThrows(ProductoNoEncontradoException.class,
                () -> useCase.obtenerProducto(99L));
    }
}
