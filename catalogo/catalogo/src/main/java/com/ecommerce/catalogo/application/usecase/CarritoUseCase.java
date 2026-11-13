package com.ecommerce.catalogo.application.usecase;

import com.ecommerce.catalogo.application.port.in.CarritoService;
import com.ecommerce.catalogo.domain.exception.CantidadInvalidaException;
import com.ecommerce.catalogo.domain.exception.CarritoNoFoundException;
import com.ecommerce.catalogo.domain.exception.CarritoVacioException;
import com.ecommerce.catalogo.domain.exception.DatosInvalidosException;
import com.ecommerce.catalogo.domain.exception.ItemCarritoNoEncontradoException;
import com.ecommerce.catalogo.domain.exception.ProductoNoEncontradoException;
import com.ecommerce.catalogo.domain.exception.StockInsuficienteException;
import com.ecommerce.catalogo.domain.exception.UsuarioNoEncontradoException;
import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.ItemCarrito;
import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.domain.model.Usuario;
import com.ecommerce.catalogo.domain.port.out.CarritoGateway;
import com.ecommerce.catalogo.domain.port.out.ProductoGateway;
import com.ecommerce.catalogo.domain.port.out.UsuarioGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Casos de uso del carrito.
 *
 * <p>Importante: el carrito <strong>no reserva stock</strong>. Solo valida
 * disponibilidad; el stock se descuenta de forma atomica al confirmar la compra.</p>
 */
@RequiredArgsConstructor
public class CarritoUseCase implements CarritoService {

    private static final int CANTIDAD_MAXIMA_POR_PRODUCTO = 10;

    private final CarritoGateway carritoGateway;
    private final ProductoGateway productoGateway;
    private final UsuarioGateway usuarioGateway;

    @Override
    @Transactional
    public Carrito agregarProductoAlCarrito(Long usuarioId, Long productoId, int cantidad) {
        validarUsuario(usuarioId);
        validarCantidad(cantidad);

        Producto producto = obtenerProducto(productoId);
        Carrito carrito = carritoGateway.buscarPorUsuarioId(usuarioId);
        if (carrito == null) {
            carrito = crearCarrito(usuarioId);
        }

        Optional<ItemCarrito> existente = carrito.getItems().stream()
                .filter(item -> item.getProductoId().equals(productoId))
                .findFirst();

        int cantidadTotal = existente.map(item -> item.getCantidad() + cantidad).orElse(cantidad);
        validarCantidad(cantidadTotal);
        validarStock(producto, cantidadTotal);

        if (existente.isPresent()) {
            ItemCarrito item = existente.get();
            item.setCantidad(cantidadTotal);
            item.setSubtotal(subtotal(producto, cantidadTotal));
            item.setNombreProducto(producto.getNombre());
            item.setPrecioUnitario(producto.getPrecio());
            item.setImagenUrl(producto.getImagenUrl());
        } else {
            ItemCarrito item = new ItemCarrito();
            item.setProductoId(productoId);
            item.setNombreProducto(producto.getNombre());
            item.setPrecioUnitario(producto.getPrecio());
            item.setCantidad(cantidad);
            item.setSubtotal(subtotal(producto, cantidad));
            item.setImagenUrl(producto.getImagenUrl());
            carrito.getItems().add(item);
        }

        recalcularTotal(carrito);
        return carritoGateway.guardar(carrito);
    }

    @Override
    @Transactional
    public Carrito actualizarCantidadProducto(Long usuarioId, Long productoId, int nuevaCantidad) {
        validarUsuario(usuarioId);
        validarCantidad(nuevaCantidad);

        Carrito carrito = carritoGateway.buscarPorUsuarioId(usuarioId);
        if (carrito == null || carrito.getItems().isEmpty()) {
            throw new CarritoVacioException("El carrito esta vacio");
        }

        ItemCarrito item = carrito.getItems().stream()
                .filter(i -> i.getProductoId().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new ItemCarritoNoEncontradoException(productoId));

        Producto producto = obtenerProducto(productoId);
        validarStock(producto, nuevaCantidad);

        item.setCantidad(nuevaCantidad);
        item.setSubtotal(subtotal(producto, nuevaCantidad));
        item.setPrecioUnitario(producto.getPrecio());
        item.setNombreProducto(producto.getNombre());
        item.setImagenUrl(producto.getImagenUrl());

        recalcularTotal(carrito);
        return carritoGateway.guardar(carrito);
    }

    @Override
    @Transactional
    public Carrito eliminarProducto(Long usuarioId, Long productoId) {
        validarUsuario(usuarioId);

        Carrito carrito = carritoGateway.buscarPorUsuarioId(usuarioId);
        if (carrito == null || carrito.getItems() == null) {
            throw new CarritoVacioException("El carrito esta vacio");
        }

        boolean eliminado = carrito.getItems()
                .removeIf(item -> item.getProductoId().equals(productoId));
        if (!eliminado) {
            throw new ItemCarritoNoEncontradoException(productoId);
        }

        recalcularTotal(carrito);
        return carritoGateway.guardar(carrito);
    }

    @Override
    @Transactional
    public Carrito vaciarCarrito(Long usuarioId) {
        validarUsuario(usuarioId);

        Carrito carrito = carritoGateway.buscarPorUsuarioId(usuarioId);
        if (carrito == null) {
            throw new CarritoNoFoundException("El usuario no tiene un carrito");
        }

        carrito.getItems().clear();
        carrito.setPrecioTotal(BigDecimal.ZERO);
        return carritoGateway.guardar(carrito);
    }

    @Override
    @Transactional(readOnly = true)
    public Carrito obtenerCarrito(Long usuarioId) {
        validarUsuario(usuarioId);

        Carrito carrito = carritoGateway.buscarPorUsuarioId(usuarioId);
        if (carrito == null) {
            return crearCarrito(usuarioId);
        }
        return carrito;
    }

    private void validarUsuario(Long usuarioId) {
        if (usuarioId == null) {
            throw new DatosInvalidosException("El usuario es obligatorio");
        }
        Usuario usuario = usuarioGateway.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + usuarioId);
        }
    }

    private static void validarCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new CantidadInvalidaException("La cantidad debe ser mayor a 0");
        }
        if (cantidad > CANTIDAD_MAXIMA_POR_PRODUCTO) {
            throw new CantidadInvalidaException("No se puede agregar mas de "
                    + CANTIDAD_MAXIMA_POR_PRODUCTO + " unidades del mismo producto");
        }
    }

    private Producto obtenerProducto(Long productoId) {
        if (productoId == null) {
            throw new DatosInvalidosException("El producto es obligatorio");
        }
        Producto producto = productoGateway.buscarPorId(productoId);
        if (producto == null) {
            throw new ProductoNoEncontradoException(
                    "Producto no encontrado con ID: " + productoId);
        }
        return producto;
    }

    private static void validarStock(Producto producto, int cantidad) {
        if (producto.getStock() == null || producto.getStock() <= 0) {
            throw new StockInsuficienteException(
                    "El producto " + producto.getNombre() + " no tiene stock disponible");
        }
        if (cantidad > producto.getStock()) {
            throw new StockInsuficienteException(
                    "Stock insuficiente para " + producto.getNombre()
                            + ". Disponible: " + producto.getStock());
        }
    }

    private static BigDecimal subtotal(Producto producto, int cantidad) {
        return producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
    }

    private static void recalcularTotal(Carrito carrito) {
        BigDecimal total = carrito.getItems().stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        carrito.setPrecioTotal(total);
    }

    private static Carrito crearCarrito(Long usuarioId) {
        Carrito carrito = new Carrito();
        carrito.setUsuarioId(usuarioId);
        carrito.setItems(new ArrayList<>());
        carrito.setPrecioTotal(BigDecimal.ZERO);
        return carrito;
    }
}
