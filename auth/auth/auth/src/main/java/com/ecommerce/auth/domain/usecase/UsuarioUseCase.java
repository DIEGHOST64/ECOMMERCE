package com.ecommerce.auth.domain.usecase;

import com.ecommerce.auth.domain.model.MensajeNotificacion;
import com.ecommerce.auth.domain.model.NotificacionEmail;
import com.ecommerce.auth.domain.model.NotificacionSMS;
import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.model.gateway.EncrypterGateway;
import com.ecommerce.auth.domain.model.gateway.NotificacionGateway;
import com.ecommerce.auth.domain.model.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioGateway usuarioGateway;
    private final EncrypterGateway encrypterGateway;
    private final NotificacionGateway notificacionGateway;

    public Usuario guardarUsuario(Usuario usuario) {
        if (usuario.getEmail() == null || usuario.getPassword() == null) {
            throw new IllegalArgumentException("Email y password son obligatorios");
        }
        String passwordEncrypted = encrypterGateway.encrypt(usuario.getPassword());
        usuario.setPassword(passwordEncrypted);
        Usuario usuarioGuardado = usuarioGateway.guardar(usuario);
        
        // Enviar notificación de bienvenida
        enviarNotificacionBienvenida(usuarioGuardado);
        
        return usuarioGuardado;
    }
    
    private void enviarNotificacionBienvenida(Usuario usuario) {
        try {
            NotificacionEmail email = NotificacionEmail.builder()
                    .destinatario(usuario.getEmail())
                    .asunto("¡Bienvenido a nuestro E-Commerce!")
                    .cuerpo("<h1>Hola " + usuario.getNombre() + "</h1>" +
                            "<p>Gracias por registrarte en nuestra plataforma.</p>" +
                            "<p>Tu cuenta ha sido creada exitosamente.</p>")
                    .html(true)
                    .build();
            
            NotificacionSMS sms = null;
            if (usuario.getTelefono() != null && !usuario.getTelefono().isEmpty()) {
                sms = NotificacionSMS.builder()
                        .numeroTelefono(usuario.getTelefono())
                        .mensaje("Hola " + usuario.getNombre() + "! Bienvenido a nuestro E-Commerce. Tu registro fue exitoso.")
                        .build();
            }
            
            MensajeNotificacion mensaje = MensajeNotificacion.builder()
                    .tipo(sms != null ? MensajeNotificacion.TipoNotificacion.AMBOS : MensajeNotificacion.TipoNotificacion.EMAIL)
                    .email(email)
                    .sms(sms)
                    .build();
            
            notificacionGateway.enviarNotificacion(mensaje);
        } catch (Exception e) {
            // Log pero no fallar el registro si la notificación falla
            System.err.println("Error enviando notificación de bienvenida: " + e.getMessage());
        }
    }

    public void eliminarPorIdUsuario(Long id){
        try{
            usuarioGateway.eliminarPorID(id);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public Usuario buscarPorIdUsuario(Long id){
        try {
            return usuarioGateway.buscarPorID(id);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new Usuario();
        }
    }

    // ⭐ MODIFICADO - Ahora encripta la contraseña
    public Usuario actualizarUsuario(Usuario usuario){
        if (usuario.getId() == null){
            throw new IllegalArgumentException("El id es obligatorio para actualizar");
        }

        // ⭐ LOGS PARA DEPURAR
        System.out.println("=== DATOS RECIBIDOS EN USE CASE ===");
        System.out.println("ID: " + usuario.getId());
        System.out.println("Email: " + usuario.getEmail());
        System.out.println("Password: " + usuario.getPassword());
        System.out.println("===================================");

        // ⭐ Si viene una contraseña, encriptarla ANTES de actualizar
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            usuario.setPassword(encrypterGateway.encrypt(usuario.getPassword()));
        }

        return usuarioGateway.actualizarUsario(usuario);
    }

    // ⭐ NUEVO MÉTODO DE LOGIN
    public Usuario login(String email, String password) {
        // Validar que email y password no sean nulos
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email y password son obligatorios");
        }

        // Buscar usuario por email
        Usuario usuario = usuarioGateway.buscarPorEmail(email);

        // Verificar si el usuario existe
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // Verificar que la contraseña sea correcta
        Boolean passwordMatch = encrypterGateway.checkPass(password, usuario.getPassword());

        if (!passwordMatch) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Si todo está bien, retornar el usuario
        return usuario;
    }
}