package com.ecommerce.catalogo.application.usecase;

import com.ecommerce.catalogo.domain.exception.CarritoVacioException;
import com.ecommerce.catalogo.domain.exception.StockInsuficienteException;
import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.Compra;
import com.ecommerce.catalogo.domain.model.ItemCarrito;
import com.ecommerce.catalogo.domain.model.Usuario;
import com.ecommerce.catalogo.domain.port.out.CarritoGateway;
import com.ecommerce.catalogo.domain.port.out.CompraGateway;
import com.ecommerce.catalogo.domain.port.out.NotificacionGateway;
import com.ecommerce.catalogo.domain.port.out.ProductoGateway;
import com.ecommerce.catalogo.domain.port.out.UsuarioGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompraUseCaseTest {

    @Mock
    private CompraGateway compraGateway;

    @Mock
    private CarritoGateway carritoGateway;

    @Mock
    private ProductoGateway productoGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private NotificacionGateway notificacionGateway;

    @InjectMocks
    private CompraUseCase useCase;

    @Test
    void checkoutDescuentaStockGuardaCompraYEliminaCarrito() {
        Usuario usuario = new Usuario(1L, "Ana", "ana@example.com", "3001234567", "USER");
        Carrito carrito = carritoConItem();

        when(usuarioGateway.buscarPorId(1L)).thenReturn(usuario);
        when(carritoGateway.buscarPorUsuarioId(1L)).thenReturn(carrito);
        when(productoGateway.descontarStock(10L, 2)).thenReturn(true);
        when(compraGateway.guardar(any(Compra.class))).thenAnswer(invocation -> {
            Compra compra = invocation.getArgument(0);
            compra.setId(99L);
            return compra;
        });

        Compra compra = useCase.realizarCompra(1L);

        assertEquals(99L, compra.getId());
        assertEquals(new BigDecimal("20.00"), compra.getTotal());
        assertEquals("COMPLETADA", compra.getEstado());
        assertEquals(1, compra.getItems().size());
        verify(productoGateway).descontarStock(10L, 2);
        verify(carritoGateway).eliminarPorUsuarioId(1L);
        verify(notificacionGateway).enviarNotificacion(any());
    }

    @Test
    void checkoutFallaYNoGuardaCompraSiNoHayStock() {
        when(usuarioGateway.buscarPorId(1L))
                .thenReturn(new Usuario(1L, "Ana", "ana@example.com", "300", "USER"));
        when(carritoGateway.buscarPorUsuarioId(1L)).thenReturn(carritoConItem());
        when(productoGateway.descontarStock(10L, 2)).thenReturn(false);

        assertThrows(StockInsuficienteException.class, () -> useCase.realizarCompra(1L));

        verify(compraGateway, never()).guardar(any());
        verify(carritoGateway, never()).eliminarPorUsuarioId(any());
        verify(notificacionGateway, never()).enviarNotificacion(any());
    }

    @Test
    void checkoutConCarritoVacioFalla() {
        when(usuarioGateway.buscarPorId(1L))
                .thenReturn(new Usuario(1L, "Ana", "ana@example.com", "300", "USER"));
        when(carritoGateway.buscarPorUsuarioId(1L)).thenReturn(null);

        assertThrows(CarritoVacioException.class, () -> useCase.realizarCompra(1L));
    }

    private static Carrito carritoConItem() {
        ItemCarrito item = new ItemCarrito();
        item.setProductoId(10L);
        item.setNombreProducto("Teclado");
        item.setPrecioUnitario(new BigDecimal("10.00"));
        item.setCantidad(2);
        item.setSubtotal(new BigDecimal("20.00"));

        Carrito carrito = new Carrito();
        carrito.setId(5L);
        carrito.setUsuarioId(1L);
        carrito.setItems(new ArrayList<>(List.of(item)));
        carrito.setPrecioTotal(new BigDecimal("20.00"));
        return carrito;
    }
}
