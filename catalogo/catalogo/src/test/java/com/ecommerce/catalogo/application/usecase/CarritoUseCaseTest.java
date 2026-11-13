package com.ecommerce.catalogo.application.usecase;

import com.ecommerce.catalogo.domain.exception.StockInsuficienteException;
import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.domain.model.Usuario;
import com.ecommerce.catalogo.domain.port.out.CarritoGateway;
import com.ecommerce.catalogo.domain.port.out.ProductoGateway;
import com.ecommerce.catalogo.domain.port.out.UsuarioGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarritoUseCaseTest {

    @Mock
    private CarritoGateway carritoGateway;

    @Mock
    private ProductoGateway productoGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private CarritoUseCase useCase;

    @Test
    void agregarAlCarritoNoReservaStock() {
        when(usuarioGateway.buscarPorId(1L))
                .thenReturn(new Usuario(1L, "Ana", "ana@example.com", "300", "USER"));
        when(productoGateway.buscarPorId(10L)).thenReturn(producto(5));
        when(carritoGateway.buscarPorUsuarioId(1L)).thenReturn(null);
        when(carritoGateway.guardar(any(Carrito.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Carrito carrito = useCase.agregarProductoAlCarrito(1L, 10L, 2);

        assertEquals(1, carrito.getItems().size());
        assertEquals(2, carrito.getItems().get(0).getCantidad());
        assertEquals(new BigDecimal("20.00"), carrito.getPrecioTotal());
        verify(productoGateway, never()).descontarStock(anyLong(), anyInt());
    }

    @Test
    void agregarAlCarritoValidaStockDisponible() {
        when(usuarioGateway.buscarPorId(1L))
                .thenReturn(new Usuario(1L, "Ana", "ana@example.com", "300", "USER"));
        when(productoGateway.buscarPorId(10L)).thenReturn(producto(1));

        assertThrows(StockInsuficienteException.class,
                () -> useCase.agregarProductoAlCarrito(1L, 10L, 2));
        verify(carritoGateway, never()).guardar(any());
    }

    @Test
    void actualizarCantidadNoTocaStock() {
        Carrito carrito = new Carrito();
        carrito.setId(5L);
        carrito.setUsuarioId(1L);
        var item = new com.ecommerce.catalogo.domain.model.ItemCarrito();
        item.setProductoId(10L);
        item.setNombreProducto("Teclado");
        item.setPrecioUnitario(new BigDecimal("10.00"));
        item.setCantidad(2);
        item.setSubtotal(new BigDecimal("20.00"));
        carrito.setItems(new ArrayList<>(java.util.List.of(item)));
        carrito.setPrecioTotal(new BigDecimal("20.00"));

        when(usuarioGateway.buscarPorId(1L))
                .thenReturn(new Usuario(1L, "Ana", "ana@example.com", "300", "USER"));
        when(carritoGateway.buscarPorUsuarioId(1L)).thenReturn(carrito);
        when(productoGateway.buscarPorId(10L)).thenReturn(producto(10));
        when(carritoGateway.guardar(any(Carrito.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Carrito actualizado = useCase.actualizarCantidadProducto(1L, 10L, 4);

        assertEquals(4, actualizado.getItems().get(0).getCantidad());
        verify(productoGateway, never()).descontarStock(anyLong(), anyInt());
    }

    private static Producto producto(int stock) {
        Producto producto = new Producto();
        producto.setId(10L);
        producto.setNombre("Teclado");
        producto.setPrecio(new BigDecimal("10.00"));
        producto.setStock(stock);
        return producto;
    }
}
