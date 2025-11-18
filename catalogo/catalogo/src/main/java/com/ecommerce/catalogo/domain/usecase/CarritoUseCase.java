package com.ecommerce.catalogo.domain.usecase;

import com.ecommerce.catalogo.domain.exception.*;
import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.ItemCarrito;
import com.ecommerce.catalogo.domain.model.Producto;
import com.ecommerce.catalogo.domain.model.gateway.CarritoGateway;
import com.ecommerce.catalogo.domain.model.gateway.UsuarioGateway;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class CarritoUseCase {

    private final CarritoGateway carritoGateway;
    private final ProductoUseCase productoUseCase;
    private final UsuarioGateway usuarioGateway;
    private static final int CANTIDAD_MAXIMA_POR_PRODUCTO = 10;

    public Carrito agregarProductoAlCarrito(Long usuarioId, ItemCarrito itemCarrito) {
        // Validar usuario
        if (!usuarioGateway.usuarioExiste(usuarioId)) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + usuarioId);
        }

        // Validar cantidad
        if (itemCarrito.getCantidad() <= 0) {
            throw new CantidadInvalidaException("La cantidad debe ser mayor a 0");
        }

        if (itemCarrito.getCantidad() > CANTIDAD_MAXIMA_POR_PRODUCTO) {
            throw new CantidadInvalidaException("No se puede agregar más de " + CANTIDAD_MAXIMA_POR_PRODUCTO + " unidades del mismo producto");
        }

        // Obtener y validar producto
        Producto producto = productoUseCase.obtenerProducto(itemCarrito.getProductoId());
        
        // Validar stock inicial
        if (producto.getStock() <= 0) {
            throw new StockInsuficienteException("El producto " + producto.getNombre() + " no tiene stock disponible");
        }

        // Validar que la cantidad solicitada no exceda el stock disponible
        if (itemCarrito.getCantidad() > producto.getStock()) {
            throw new StockInsuficienteException("Stock insuficiente. Stock disponible para " + producto.getNombre() + ": " + producto.getStock());
        }

        // Configurar item del carrito
        itemCarrito.setNombreProducto(producto.getNombre());
        itemCarrito.setPrecioUnitario(producto.getPrecio());
        itemCarrito.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(itemCarrito.getCantidad())));

        // Obtener o crear carrito
        Carrito carrito = carritoGateway.buscarPorUsuarioId(usuarioId);
        if (carrito == null) {
            carrito = new Carrito();
            carrito.setUsuarioId(usuarioId);
            carrito.setItems(new ArrayList<>());
        }

        // Buscar si el producto ya existe en el carrito
        Optional<ItemCarrito> existente = carrito.getItems()
                .stream()
                .filter(item -> item.getProductoId().equals(itemCarrito.getProductoId()))
                .findFirst();

        // Validar stock total si el producto ya existe en el carrito
        if (existente.isPresent()) {
            int cantidadTotal = existente.get().getCantidad() + itemCarrito.getCantidad();
            if (cantidadTotal > CANTIDAD_MAXIMA_POR_PRODUCTO) {
                throw new CantidadInvalidaException("No se puede tener más de " + CANTIDAD_MAXIMA_POR_PRODUCTO + " unidades del mismo producto");
            }
            if (cantidadTotal > producto.getStock()) {
                throw new StockInsuficienteException("Stock insuficiente. Stock disponible: " + producto.getStock());
            }
            // Actualizar cantidad y subtotal
            int cantidadAdicional = itemCarrito.getCantidad();
            existente.get().setCantidad(cantidadTotal);
            existente.get().setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(cantidadTotal)));
            // Actualizar stock
            productoUseCase.actualizarStock(itemCarrito.getProductoId(), cantidadAdicional);
        } else {
            // Validar stock para nuevo item
            if (itemCarrito.getCantidad() > producto.getStock()) {
                throw new StockInsuficienteException("Stock insuficiente. Stock disponible: " + producto.getStock());
            }
            carrito.getItems().add(itemCarrito);
            // Actualizar stock
            productoUseCase.actualizarStock(itemCarrito.getProductoId(), itemCarrito.getCantidad());
        }

        // Calcular precio total del carrito
        BigDecimal precioTotal = carrito.getItems().stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        carrito.setPrecioTotal(precioTotal);

        return carritoGateway.guardar(carrito);
    }

    public Carrito actualizarCantidadProducto(Long usuarioId, Long productoId, Integer nuevaCantidad) {
        // Validar usuario
        if (!usuarioGateway.usuarioExiste(usuarioId)) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + usuarioId);
        }

        // Validar cantidad
        if (nuevaCantidad <= 0) {
            throw new CantidadInvalidaException("La cantidad debe ser mayor a 0");
        }

        if (nuevaCantidad > CANTIDAD_MAXIMA_POR_PRODUCTO) {
            throw new CantidadInvalidaException("No se puede agregar más de " + CANTIDAD_MAXIMA_POR_PRODUCTO + " unidades del mismo producto");
        }

        // Validar carrito
        Carrito carrito = carritoGateway.buscarPorUsuarioId(usuarioId);
        if (carrito == null || carrito.getItems().isEmpty()) {
            throw new CarritoVacioException("El carrito está vacío");
        }

        // Encontrar el item en el carrito
        ItemCarrito item = carrito.getItems().stream()
                .filter(i -> i.getProductoId().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));

        // Obtener producto y validar stock
        Producto producto = productoUseCase.obtenerProducto(productoId);
        
        // Calcular la diferencia de cantidades para actualizar el stock
        int diferenciaCantidad = nuevaCantidad - item.getCantidad();
        
        // Si vamos a aumentar la cantidad, verificar que hay suficiente stock
        if (diferenciaCantidad > 0) {
            int stockNecesario = diferenciaCantidad;
            if (stockNecesario > producto.getStock()) {
                throw new StockInsuficienteException("Stock insuficiente. Stock disponible: " + producto.getStock());
            }
            // Actualizar stock (restar)
            productoUseCase.actualizarStock(productoId, diferenciaCantidad);
        } else if (diferenciaCantidad < 0) {
            // Si vamos a disminuir la cantidad, restaurar el stock
            productoUseCase.restaurarStock(productoId, Math.abs(diferenciaCantidad));
        }

        // Actualizar item
        item.setCantidad(nuevaCantidad);
        item.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(nuevaCantidad)));

        BigDecimal precioTotal = carrito.getItems().stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        carrito.setPrecioTotal(precioTotal);

        return carritoGateway.guardar(carrito);
    }

    public Carrito eliminarProducto(Long usuarioId, Long productoId) {
        if (!usuarioGateway.usuarioExiste(usuarioId)) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + usuarioId);
        }

        Carrito carrito = carritoGateway.buscarPorUsuarioId(usuarioId);
        if (carrito == null || carrito.getItems() == null) {
            throw new CarritoVacioException("El carrito está vacío");
        }

        List<ItemCarrito> itemsActualizados = new ArrayList<>(carrito.getItems());
        boolean productoEncontrado = false;

        for (Iterator<ItemCarrito> iterator = itemsActualizados.iterator(); iterator.hasNext();) {
            ItemCarrito item = iterator.next();
            if (item.getProductoId().equals(productoId)) {
                iterator.remove();
                productoEncontrado = true;
                break;
            }
        }

        if (!productoEncontrado) {
            throw new RuntimeException("Producto no encontrado en el carrito");
        }

        // Restaurar el stock del producto eliminado
        for (ItemCarrito item : carrito.getItems()) {
            if (item.getProductoId().equals(productoId)) {
                productoUseCase.restaurarStock(productoId, item.getCantidad());
                break;
            }
        }

        carrito.setItems(itemsActualizados);
        BigDecimal precioTotal = itemsActualizados.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        carrito.setPrecioTotal(precioTotal);

        return carritoGateway.guardar(carrito);
    }

    public Carrito vaciarCarrito(Long usuarioId) {
        if (!usuarioGateway.usuarioExiste(usuarioId)) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + usuarioId);
        }

        Carrito carrito = carritoGateway.buscarPorUsuarioId(usuarioId);
        if (carrito == null) {
            throw new CarritoVacioException("El carrito está vacío");
        }

        // Restaurar el stock de todos los productos en el carrito
        for (ItemCarrito item : carrito.getItems()) {
            productoUseCase.restaurarStock(item.getProductoId(), item.getCantidad());
        }

        // Limpiar el carrito
        carrito.getItems().clear();
        carrito.setPrecioTotal(BigDecimal.ZERO);
        return carritoGateway.guardar(carrito);
    }

    public Carrito obtenerCarrito(Long usuarioId) {
        // Validación inicial de parámetros
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo");
        }

        // Doble validación: primero verificamos si el usuario existe
        boolean usuarioExiste = usuarioGateway.usuarioExiste(usuarioId);
        if (!usuarioExiste) {
            // Eliminar cualquier carrito que exista para este usuario no válido
            carritoGateway.eliminarPorUsuarioId(usuarioId);
            throw new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + usuarioId);
        }

        // Si el usuario existe, procedemos con el carrito
        Carrito carrito = carritoGateway.buscarPorUsuarioId(usuarioId);
        
        // Si no existe el carrito y el usuario es válido, crear uno nuevo
        if (carrito == null) {
            carrito = new Carrito();
            carrito.setUsuarioId(usuarioId);
            carrito.setItems(new ArrayList<>());
            carrito.setPrecioTotal(BigDecimal.ZERO);
            // Guardar el carrito nuevo
            carrito = carritoGateway.guardar(carrito);
        }

        return carrito;
    }
}
