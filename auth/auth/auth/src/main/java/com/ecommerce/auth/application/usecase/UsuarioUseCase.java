package com.ecommerce.auth.application.usecase;

import com.ecommerce.auth.application.model.LoginResult;
import com.ecommerce.auth.application.port.in.UsuarioService;
import com.ecommerce.auth.domain.exception.CredencialesInvalidasException;
import com.ecommerce.auth.domain.exception.DatosInvalidosException;
import com.ecommerce.auth.domain.exception.EmailEnUsoException;
import com.ecommerce.auth.domain.exception.UsuarioNoEncontradoException;
import com.ecommerce.auth.domain.model.MensajeNotificacion;
import com.ecommerce.auth.domain.model.NotificacionEmail;
import com.ecommerce.auth.domain.model.NotificacionSMS;
import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.port.out.EncrypterGateway;
import com.ecommerce.auth.domain.port.out.NotificacionGateway;
import com.ecommerce.auth.domain.port.out.TokenGateway;
import com.ecommerce.auth.domain.port.out.UsuarioGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class UsuarioUseCase implements UsuarioService {

    public static final String ROL_CLIENTE = "USER";
    private static final int PASSWORD_MIN_LENGTH = 6;

    private final UsuarioGateway usuarioGateway;
    private final EncrypterGateway encrypterGateway;
    private final NotificacionGateway notificacionGateway;
    private final TokenGateway tokenGateway;

    @Override
    @Transactional
    public Usuario registrar(Usuario usuario) {
        if (usuario == null) {
            throw new DatosInvalidosException("Los datos del usuario son obligatorios");
        }

        String nombre = trimToNull(usuario.getNombre());
        if (nombre == null) {
            throw new DatosInvalidosException("El nombre es obligatorio");
        }

        String email = normalizarEmail(usuario.getEmail());
        if (email == null) {
            throw new DatosInvalidosException("El email es obligatorio");
        }

        validarPassword(usuario.getPassword());

        if (usuarioGateway.buscarPorEmail(email) != null) {
            throw new EmailEnUsoException(email);
        }

        usuario.setId(null);
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        // El rol jamas se toma del cliente: todo registro publico es cliente.
        usuario.setRole(ROL_CLIENTE);
        usuario.setPassword(encrypterGateway.encrypt(usuario.getPassword()));
        usuario.setTelefono(trimToNull(usuario.getTelefono()));

        Usuario guardado = usuarioGateway.guardar(usuario);
        notificarBienvenida(guardado);
        return guardado;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResult login(String email, String password) {
        String emailNormalizado = normalizarEmail(email);
        if (emailNormalizado == null || password == null || password.isBlank()) {
            throw new CredencialesInvalidasException();
        }

        Usuario usuario = usuarioGateway.buscarPorEmail(emailNormalizado);
        if (usuario == null || !encrypterGateway.checkPass(password, usuario.getPassword())) {
            // Mensaje generico para no revelar si el email existe.
            throw new CredencialesInvalidasException();
        }

        return new LoginResult(
                usuario,
                tokenGateway.generarToken(usuario),
                tokenGateway.expiracionEnSegundos());
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        if (id == null) {
            throw new DatosInvalidosException("El id es obligatorio");
        }
        Usuario usuario = usuarioGateway.buscarPorID(id);
        if (usuario == null) {
            throw new UsuarioNoEncontradoException(id);
        }
        return usuario;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorId(Long id) {
        return id != null && usuarioGateway.buscarPorID(id) != null;
    }

    @Override
    @Transactional
    public Usuario actualizar(Usuario cambios) {
        if (cambios == null || cambios.getId() == null) {
            throw new DatosInvalidosException("El id es obligatorio para actualizar");
        }

        Usuario existente = buscarPorId(cambios.getId());

        String nombre = trimToNull(cambios.getNombre());
        if (nombre != null) {
            existente.setNombre(nombre);
        }

        String email = normalizarEmail(cambios.getEmail());
        if (email != null && !email.equals(existente.getEmail())) {
            if (usuarioGateway.buscarPorEmail(email) != null) {
                throw new EmailEnUsoException(email);
            }
            existente.setEmail(email);
        }

        if (cambios.getTelefono() != null) {
            existente.setTelefono(trimToNull(cambios.getTelefono()));
        }
        if (cambios.getEdad() != null) {
            existente.setEdad(cambios.getEdad());
        }
        if (cambios.getPassword() != null && !cambios.getPassword().isBlank()) {
            validarPassword(cambios.getPassword());
            existente.setPassword(encrypterGateway.encrypt(cambios.getPassword()));
        }

        // El rol y el id se conservan del registro existente; nunca se aceptan del cliente.
        return usuarioGateway.actualizarUsario(existente);
    }

    @Override
    @Transactional
    public void eliminarPorId(Long id) {
        Usuario usuario = buscarPorId(id);
        usuarioGateway.eliminarPorID(usuario.getId());
    }

    private void validarPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new DatosInvalidosException("La contrasena es obligatoria");
        }
        if (password.length() < PASSWORD_MIN_LENGTH) {
            throw new DatosInvalidosException(
                    "La contrasena debe tener al menos " + PASSWORD_MIN_LENGTH + " caracteres");
        }
    }

    private void notificarBienvenida(Usuario usuario) {
        try {
            NotificacionEmail email = NotificacionEmail.builder()
                    .destinatario(usuario.getEmail())
                    .asunto("Bienvenido a nuestro E-Commerce")
                    .cuerpo("<h1>Hola " + usuario.getNombre() + "</h1>"
                            + "<p>Gracias por registrarte en nuestra plataforma.</p>"
                            + "<p>Tu cuenta ha sido creada exitosamente.</p>")
                    .html(true)
                    .build();

            NotificacionSMS sms = null;
            if (usuario.getTelefono() != null && !usuario.getTelefono().isBlank()) {
                sms = NotificacionSMS.builder()
                        .numeroTelefono(usuario.getTelefono())
                        .mensaje("Hola " + usuario.getNombre()
                                + "! Bienvenido a nuestro E-Commerce. Tu registro fue exitoso.")
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
            // Una falla del broker no debe impedir el registro.
            log.warn("No se pudo encolar la notificacion de bienvenida para {}: {}",
                    usuario.getEmail(), e.getMessage());
        }
    }

    private static String normalizarEmail(String email) {
        String valor = trimToNull(email);
        return valor == null ? null : valor.toLowerCase(Locale.ROOT);
    }

    private static String trimToNull(String valor) {
        if (valor == null) {
            return null;
        }
        String trimmed = valor.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
