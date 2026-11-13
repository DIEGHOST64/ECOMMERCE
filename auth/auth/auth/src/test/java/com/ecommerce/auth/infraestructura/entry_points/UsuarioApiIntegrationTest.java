package com.ecommerce.auth.infraestructura.entry_points;

import com.ecommerce.auth.domain.port.out.NotificacionGateway;
import com.ecommerce.auth.infraestructura.driver_adapters.jpa_repository.UsuarioData;
import com.ecommerce.auth.infraestructura.driver_adapters.jpa_repository.UsuarioDataJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioDataJpaRepository repository;

    @MockitoBean
    private NotificacionGateway notificacionGateway;

    @BeforeEach
    void limpiarBaseDeDatos() {
        repository.deleteAll();
    }

    @Test
    void registroYLoginDevuelvenTokenSinExponerPassword() throws Exception {
        mockMvc.perform(post("/api/ecommerce/usuario/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson("ana@example.com", "secret123")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.email").value("ana@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(post("/api/ecommerce/usuario/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("ana@example.com", "secret123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void endpointProtegidoRechazaPeticionesSinToken() throws Exception {
        mockMvc.perform(get("/api/ecommerce/usuario/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioNoPuedeLeerDatosDeOtroUsuario() throws Exception {
        Sesion sesion = registrarYLogin("ana@example.com");

        mockMvc.perform(get("/api/ecommerce/usuario/{id}", sesion.id() + 999)
                        .header("Authorization", "Bearer " + sesion.token()))
                .andExpect(status().isForbidden());
    }

    @Test
    void usuarioPuedeLeerSusPropiosDatos() throws Exception {
        Sesion sesion = registrarYLogin("ana@example.com");

        mockMvc.perform(get("/api/ecommerce/usuario/{id}", sesion.id())
                        .header("Authorization", "Bearer " + sesion.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sesion.id()))
                .andExpect(jsonPath("$.email").value("ana@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void usuarioPuedeActualizarSuNombrePeroNoCambiarSuRol() throws Exception {
        Sesion sesion = registrarYLogin("ana@example.com");

        mockMvc.perform(put("/api/ecommerce/usuario/update")
                        .header("Authorization", "Bearer " + sesion.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "id", sesion.id(),
                                "nombre", "Ana Maria",
                                "role", "ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana Maria"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void usuarioNoPuedeEliminarSuCuentaSinSerAdmin() throws Exception {
        Sesion sesion = registrarYLogin("ana@example.com");

        mockMvc.perform(delete("/api/ecommerce/usuario/{id}", sesion.id())
                        .header("Authorization", "Bearer " + sesion.token()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminPuedeEliminarUsuarios() throws Exception {
        Sesion sesion = registrarYLogin("ana@example.com");

        UsuarioData data = repository.findByEmailIgnoreCase("ana@example.com").orElseThrow();
        data.setRole("ADMIN");
        repository.save(data);

        Sesion admin = login("ana@example.com", "secret123");

        mockMvc.perform(delete("/api/ecommerce/usuario/{id}", sesion.id())
                        .header("Authorization", "Bearer " + admin.token()))
                .andExpect(status().isNoContent());
    }

    @Test
    void registroDuplicadoDevuelveConflicto() throws Exception {
        mockMvc.perform(post("/api/ecommerce/usuario/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson("ana@example.com", "secret123")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/ecommerce/usuario/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson("ANA@example.com", "secret123")))
                .andExpect(status().isConflict());
    }

    @Test
    void loginIncorrectoDevuelveMensajeGenerico() throws Exception {
        mockMvc.perform(post("/api/ecommerce/usuario/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("nadie@example.com", "secret123")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Email o contrasena incorrectos"));
    }

    @Test
    void validacionDevuelveErroresPorCampo() throws Exception {
        mockMvc.perform(post("/api/ecommerce/usuario/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "nombre", "",
                                "email", "no-es-email",
                                "password", "123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists())
                .andExpect(jsonPath("$.fieldErrors.nombre").exists());
    }

    private Sesion registrarYLogin(String email) throws Exception {
        mockMvc.perform(post("/api/ecommerce/usuario/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson(email, "secret123")))
                .andExpect(status().isCreated());

        return login(email, "secret123");
    }

    private Sesion login(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/ecommerce/usuario/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(email, password)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return new Sesion(body.get("id").asLong(), body.get("token").asText());
    }

    private String registroJson(String email, String password) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "nombre", "Ana",
                "email", email,
                "password", password,
                "telefono", "3001234567",
                "edad", 30));
    }

    private String loginJson(String email, String password) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "email", email,
                "password", password));
    }

    private record Sesion(Long id, String token) {
    }
}
