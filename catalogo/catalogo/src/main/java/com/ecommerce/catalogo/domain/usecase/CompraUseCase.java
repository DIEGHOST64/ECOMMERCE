package com.ecommerce.catalogo.domain.usecase;

import com.ecommerce.catalogo.domain.exception.CarritoVacioException;
import com.ecommerce.catalogo.domain.exception.UsuarioNoEncontradoException;
import com.ecommerce.catalogo.domain.model.Carrito;
import com.ecommerce.catalogo.domain.model.Compra;
import com.ecommerce.catalogo.domain.model.ItemCarrito;
import com.ecommerce.catalogo.domain.model.Usuario;
import com.ecommerce.catalogo.domain.model.gateway.CarritoGateway;
import com.ecommerce.catalogo.domain.model.gateway.CompraGateway;
import com.ecommerce.catalogo.domain.model.gateway.UsuarioGateway;
import com.ecommerce.catalogo.infraestructure.message_broker.MensajeCola;
import com.ecommerce.catalogo.infraestructure.message_broker.NotificacionEmail;
import com.ecommerce.catalogo.infraestructure.message_broker.NotificacionSMS;
import com.ecommerce.catalogo.infraestructure.message_broker.SqsProducer;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CompraUseCase {

    private final CompraGateway compraGateway;
    private final CarritoGateway carritoGateway;
    private final CarritoUseCase carritoUseCase;
    private final UsuarioGateway usuarioGateway;
    private final SqsProducer sqsProducer;

    public Compra realizarCompra(Long usuarioId) {
        // Validar que el usuario existe
        if (!usuarioGateway.usuarioExiste(usuarioId)) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + usuarioId);
        }

        // Obtener el carrito del usuario
        Carrito carrito = carritoGateway.buscarPorUsuarioId(usuarioId);
        if (carrito == null || carrito.getItems().isEmpty()) {
            throw new CarritoVacioException("No hay items en el carrito para realizar la compra");
        }

        // Crear la compra con nuevos items (para evitar problemas de persistencia)
        Compra compra = new Compra();
        compra.setUsuarioId(usuarioId);
        compra.setFechaCompra(LocalDateTime.now());
        compra.setItems(new ArrayList<>(carrito.getItems())); // Crear nueva lista con los items
        compra.setTotal(carrito.getPrecioTotal());
        compra.setEstado("COMPLETADA");

        // Guardar la compra
        Compra compraGuardada = compraGateway.guardar(compra);

        // Enviar notificación de compra completada
        enviarNotificacionCompra(compraGuardada, usuarioId);

        // Vaciar el carrito (esto ya maneja la actualización del stock)
        carritoUseCase.vaciarCarrito(usuarioId);

        return compraGuardada;
    }
    
    private void enviarNotificacionCompra(Compra compra, Long usuarioId) {
        try {
            // Obtener información del usuario
            Usuario usuario = usuarioGateway.buscarPorId(usuarioId);
            if (usuario == null) {
                return;
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String fechaFormateada = compra.getFechaCompra().format(formatter);
            
            // Crear notificación por email
            NotificacionEmail email = NotificacionEmail.builder()
                    .destinatario(usuario.getEmail())
                    .asunto("Confirmación de Compra #" + compra.getId())
                    .cuerpo("<h1>¡Compra Confirmada!</h1>" +
                            "<p>Hola " + usuario.getNombre() + ",</p>" +
                            "<p>Tu compra ha sido procesada exitosamente.</p>" +
                            "<p><strong>Número de orden:</strong> " + compra.getId() + "</p>" +
                            "<p><strong>Fecha:</strong> " + fechaFormateada + "</p>" +
                            "<p><strong>Total:</strong> $" + String.format("%.2f", compra.getTotal()) + "</p>" +
                            "<p>Gracias por tu compra.</p>")
                    .html(true)
                    .build();
            
            // Crear notificación por SMS si tiene teléfono
            NotificacionSMS sms = null;
            if (usuario.getTelefono() != null && !usuario.getTelefono().isEmpty()) {
                sms = NotificacionSMS.builder()
                        .numeroTelefono(usuario.getTelefono())
                        .mensaje("Compra #" + compra.getId() + " confirmada por $" + 
                                String.format("%.2f", compra.getTotal()) + ". Gracias por tu compra!")
                        .build();
            }
            
            MensajeCola mensaje = MensajeCola.builder()
                    .tipo(sms != null ? MensajeCola.TipoNotificacion.AMBOS : MensajeCola.TipoNotificacion.EMAIL)
                    .email(email)
                    .sms(sms)
                    .build();
            
            sqsProducer.enviarMensaje(mensaje);
        } catch (Exception e) {
            // Log pero no fallar la compra si la notificación falla
            System.err.println("Error enviando notificación de compra: " + e.getMessage());
        }
    }

    public List<Compra> obtenerComprasPorUsuario(Long usuarioId) {
        if (!usuarioGateway.usuarioExiste(usuarioId)) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + usuarioId);
        }
        return compraGateway.buscarPorUsuarioId(usuarioId);
    }

    public Compra obtenerCompraPorId(Long compraId) {
        return compraGateway.buscarPorId(compraId);
    }

    public List<Compra> obtenerTodasLasCompras() {
        return compraGateway.obtenerTodas();
    }
}