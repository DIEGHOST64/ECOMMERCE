package com.ecommerce.catalogo.application.usecase;

import com.ecommerce.catalogo.application.port.in.CompraService;
import com.ecommerce.catalogo.domain.exception.CarritoVacioException;
import com.ecommerce.catalogo.domain.exception.CompraNoEncontradaException;
import com.ecommerce.catalogo.domain.exception.StockInsuficienteException;
import com.ecommerce.catalogo.domain.exception.UsuarioNoEncontradoException;
import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.Compra;
import com.ecommerce.catalogo.domain.model.ItemCarrito;
import com.ecommerce.catalogo.domain.model.MensajeNotificacion;
import com.ecommerce.catalogo.domain.model.NotificacionEmail;
import com.ecommerce.catalogo.domain.model.NotificacionSMS;
import com.ecommerce.catalogo.domain.model.Usuario;
import com.ecommerce.catalogo.domain.port.out.CarritoGateway;
import com.ecommerce.catalogo.domain.port.out.CompraGateway;
import com.ecommerce.catalogo.domain.port.out.NotificacionGateway;
import com.ecommerce.catalogo.domain.port.out.ProductoGateway;
import com.ecommerce.catalogo.domain.port.out.UsuarioGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CompraUseCase implements CompraService {

    private final CompraGateway compraGateway;
    private final CarritoGateway carritoGateway;
    private final ProductoGateway productoGateway;
    private final UsuarioGateway usuarioGateway;
    private final NotificacionGateway notificacionGateway;

    @Override
    @Transactional
    public Compra realizarCompra(Long usuarioId) {
        Usuario usuario = usuarioGateway.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + usuarioId);
        }

        Carrito carrito = carritoGateway.buscarPorUsuarioId(usuarioId);
        if (carrito == null || carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new CarritoVacioException(
                    "No hay items en el carrito para realizar la compra");
        }

        // Descuento atomico: si un item no tiene stock, la transaccion revierte todo.
        for (ItemCarrito item : carrito.getItems()) {
            if (!productoGateway.descontarStock(item.getProductoId(), item.getCantidad())) {
                throw new StockInsuficienteException(
                        "Stock insuficiente para el producto " + item.getNombreProducto());
            }
        }

        Compra compra = construirCompra(usuarioId, carrito);
        Compra compraGuardada = compraGateway.guardar(compra);

        // El carrito ya no se necesita: el stock se desconto en el checkout.
        carritoGateway.eliminarPorUsuarioId(usuarioId);

        enviarNotificacionDespuesDelCommit(compraGuardada, usuario);
        return compraGuardada;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Compra> obtenerComprasPorUsuario(Long usuarioId) {
        if (usuarioGateway.buscarPorId(usuarioId) == null) {
            throw new UsuarioNoEncontradoException(
                    "Usuario no encontrado con ID: " + usuarioId);
        }
        return compraGateway.buscarPorUsuarioId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Compra obtenerCompraPorId(Long compraId) {
        Compra compra = compraGateway.buscarPorId(compraId);
        if (compra == null) {
            throw new CompraNoEncontradaException(compraId);
        }
        return compra;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Compra> obtenerTodasLasCompras() {
        return compraGateway.obtenerTodas();
    }

    private static Compra construirCompra(Long usuarioId, Carrito carrito) {
        Compra compra = new Compra();
        compra.setUsuarioId(usuarioId);
        compra.setFechaCompra(LocalDateTime.now());
        compra.setEstado("COMPLETADA");

        List<ItemCarrito> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (ItemCarrito item : carrito.getItems()) {
            ItemCarrito copia = new ItemCarrito();
            copia.setProductoId(item.getProductoId());
            copia.setNombreProducto(item.getNombreProducto());
            copia.setPrecioUnitario(item.getPrecioUnitario());
            copia.setCantidad(item.getCantidad());
            BigDecimal subtotal = item.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(item.getCantidad()));
            copia.setSubtotal(subtotal);
            copia.setImagenUrl(item.getImagenUrl());
            items.add(copia);
            total = total.add(subtotal);
        }

        compra.setItems(items);
        compra.setTotal(total);
        return compra;
    }

    private void enviarNotificacionDespuesDelCommit(Compra compra, Usuario usuario) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            enviarNotificacionCompra(compra, usuario);
                        }
                    });
        } else {
            enviarNotificacionCompra(compra, usuario);
        }
    }

    private void enviarNotificacionCompra(Compra compra, Usuario usuario) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            NotificacionEmail email = NotificacionEmail.builder()
                    .destinatario(usuario.getEmail())
                    .asunto("Confirmacion de Compra #" + compra.getId())
                    .cuerpo("<h1>Compra confirmada</h1>"
                            + "<p>Hola " + usuario.getNombre() + ",</p>"
                            + "<p>Tu compra ha sido procesada exitosamente.</p>"
                            + "<p><strong>Numero de orden:</strong> " + compra.getId() + "</p>"
                            + "<p><strong>Fecha:</strong> "
                            + compra.getFechaCompra().format(formatter) + "</p>"
                            + "<p><strong>Total:</strong> $" + compra.getTotal() + "</p>"
                            + "<p>Gracias por tu compra.</p>")
                    .html(true)
                    .build();

            NotificacionSMS sms = null;
            if (usuario.getTelefono() != null && !usuario.getTelefono().isBlank()) {
                String productos = resumenProductos(compra.getItems());
                sms = NotificacionSMS.builder()
                        .numeroTelefono(usuario.getTelefono())
                        .mensaje("Hola " + usuario.getNombre() + "! Tu compra #" + compra.getId()
                                + " ha sido confirmada. Productos: " + productos
                                + ". Total: $" + compra.getTotal()
                                + ". Gracias por tu preferencia!")
                        .build();
            }

            MensajeNotificacion mensaje = MensajeNotificacion.builder()
                    .tipo(sms != null
                            ? MensajeNotificacion.TipoNotificacion.AMBOS
                            : MensajeNotificacion.TipoNotificacion.EMAIL)
                    .email(email)
                    .sms(sms)
                    .build();

            notificacionGateway.enviarNotificacion(mensaje);
        } catch (Exception e) {
            log.warn("No se pudo encolar la notificacion de la compra {}: {}",
                    compra.getId(), e.getMessage());
        }
    }

    private static String resumenProductos(List<ItemCarrito> items) {
        StringBuilder texto = new StringBuilder();
        int maxProductos = Math.min(3, items.size());
        for (int i = 0; i < maxProductos; i++) {
            texto.append(items.get(i).getNombreProducto());
            if (i < maxProductos - 1) {
                texto.append(", ");
            }
        }
        if (items.size() > maxProductos) {
            texto.append(" y ").append(items.size() - maxProductos).append(" mas");
        }
        return texto.toString();
    }
}
