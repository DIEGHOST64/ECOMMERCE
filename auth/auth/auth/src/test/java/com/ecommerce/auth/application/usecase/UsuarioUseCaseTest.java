package com.ecommerce.auth.application.usecase;

import com.ecommerce.auth.application.model.LoginResult;
import com.ecommerce.auth.domain.exception.CredencialesInvalidasException;
import com.ecommerce.auth.domain.exception.EmailEnUsoException;
import com.ecommerce.auth.domain.exception.UsuarioNoEncontradoException;
import com.ecommerce.auth.domain.model.Usuario;
import com.ecommerce.auth.domain.port.out.EncrypterGateway;
import com.ecommerce.auth.domain.port.out.NotificacionGateway;
import com.ecommerce.auth.domain.port.out.TokenGateway;
import com.ecommerce.auth.domain.port.out.UsuarioGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private EncrypterGateway encrypterGateway;

    @Mock
    private NotificacionGateway notificacionGateway;

    @Mock
    private TokenGateway tokenGateway;

    @InjectMocks
    private UsuarioUseCase useCase;

    @Test
    void registraUsuarioNormalizandoEmailYForzandoRolCliente() {
        Usuario entrada = new Usuario(null, "  Ana  ", "ANA@Example.COM ", "secret123",
                " 3001234567 ", "ADMIN", 25);

        when(usuarioGateway.buscarPorEmail("ana@example.com")).thenReturn(null);
        when(encrypterGateway.encrypt("secret123")).thenReturn("HASH");
        when(usuarioGateway.guardar(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario guardado = invocation.getArgument(0);
            guardado.setId(1L);
            return guardado;
        });

        Usuario resultado = useCase.registrar(entrada);

        assertEquals(1L, resultado.getId());
        assertEquals("Ana", resultado.getNombre());
        assertEquals("ana@example.com", resultado.getEmail());
        assertEquals("USER", resultado.getRole());
        assertEquals("HASH", resultado.getPassword());
        assertEquals("3001234567", resultado.getTelefono());

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioGateway).guardar(captor.capture());
        assertEquals("USER", captor.getValue().getRole());
        verify(notificacionGateway).enviarNotificacion(any());
    }

    @Test
    void rechazaRegistroConEmailDuplicado() {
        Usuario entrada = new Usuario(null, "Ana", "ana@example.com", "secret123",
                null, null, 25);
        when(usuarioGateway.buscarPorEmail("ana@example.com"))
                .thenReturn(new Usuario());

        assertThrows(EmailEnUsoException.class, () -> useCase.registrar(entrada));
    }

    @Test
    void loginDevuelveTokenCuandoLasCredencialesSonCorrectas() {
        Usuario usuario = new Usuario(1L, "Ana", "ana@example.com", "HASH",
                null, "USER", 25);

        when(usuarioGateway.buscarPorEmail("ana@example.com")).thenReturn(usuario);
        when(encrypterGateway.checkPass("secret123", "HASH")).thenReturn(true);
        when(tokenGateway.generarToken(usuario)).thenReturn("jwt-token");
        when(tokenGateway.expiracionEnSegundos()).thenReturn(3600L);

        LoginResult resultado = useCase.login(" ANA@example.com ", "secret123");

        assertEquals("jwt-token", resultado.token());
        assertEquals(3600L, resultado.expiraEnSegundos());
        assertEquals(usuario, resultado.usuario());
    }

    @Test
    void loginFallaConMensajeGenericoSiElUsuarioNoExiste() {
        when(usuarioGateway.buscarPorEmail("nadie@example.com")).thenReturn(null);

        CredencialesInvalidasException ex = assertThrows(CredencialesInvalidasException.class,
                () -> useCase.login("nadie@example.com", "secret123"));

        assertEquals("Email o contrasena incorrectos", ex.getMessage());
        verify(encrypterGateway, never()).checkPass(anyString(), anyString());
    }

    @Test
    void loginFallaConMensajeGenericoSiLaContrasenaEsIncorrecta() {
        Usuario usuario = new Usuario(1L, "Ana", "ana@example.com", "HASH",
                null, "USER", 25);
        when(usuarioGateway.buscarPorEmail("ana@example.com")).thenReturn(usuario);
        when(encrypterGateway.checkPass("mala", "HASH")).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class,
                () -> useCase.login("ana@example.com", "mala"));
    }

    @Test
    void actualizarConservaRolYPasswordCuandoNoSeEnvianCambios() {
        Usuario existente = new Usuario(1L, "Ana", "ana@example.com", "HASH",
                "300", "ADMIN", 25);
        Usuario cambios = new Usuario();
        cambios.setId(1L);
        cambios.setNombre("Ana Maria");
        cambios.setRole("USER");
        cambios.setPassword("");

        when(usuarioGateway.buscarPorID(1L)).thenReturn(existente);
        when(usuarioGateway.actualizarUsario(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = useCase.actualizar(cambios);

        assertEquals("Ana Maria", resultado.getNombre());
        assertEquals("ADMIN", resultado.getRole());
        assertEquals("HASH", resultado.getPassword());
        verify(encrypterGateway, never()).encrypt(anyString());
    }

    @Test
    void eliminarFallaSiElUsuarioNoExiste() {
        when(usuarioGateway.buscarPorID(99L)).thenReturn(null);

        assertThrows(UsuarioNoEncontradoException.class, () -> useCase.eliminarPorId(99L));
    }

    @Test
    void existePorIdRetornaFalseParaIdNulo() {
        assertTrue(!useCase.existePorId(null));
    }
}
