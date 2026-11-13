package com.ecommerce.catalogo.infraestructura.entry_points;

import com.ecommerce.catalogo.domain.model.Usuario;
import com.ecommerce.catalogo.domain.port.out.NotificacionGateway;
import com.ecommerce.catalogo.domain.port.out.UsuarioGateway;
import com.ecommerce.catalogo.infraestructura.driver_adapters.jpa_repository.carrito.CarritoDataJpaRepository;
import com.ecommerce.catalogo.infraestructura.driver_adapters.jpa_repository.compra.CompraDataJpaRepository;
import com.ecommerce.catalogo.infraestructura.driver_adapters.jpa_repository.producto.ProductoData;
import com.ecommerce.catalogo.infraestructura.driver_adapters.jpa_repository.producto.ProductoDataJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TiendaApiIntegrationTest {

    private static final String SECRET = "test-secret-with-at-least-32-characters-0001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductoDataJpaRepository productoRepository;

    @Autowired
    private CarritoDataJpaRepository carritoRepository;

    @Autowired
    private CompraDataJpaRepository compraRepository;

    @MockitoBean
    private UsuarioGateway usuarioGateway;

    @MockitoBean
    private NotificacionGateway notificacionGateway;

    @BeforeEach
    void limpiarBaseDeDatos() {
        compraRepository.deleteAll();
        carritoRepository.deleteAll();
        productoRepository.deleteAll();
    }

    @Test
    void losProductosSonPublicos() throws Exception {
        ProductoData producto = crearProducto("Teclado", "10.00", 5);

        mockMvc.perform(get("/api/ecommerce/producto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(producto.getId()))
                .andExpect(jsonPath("$[0].nombre").value("Teclado"));

        mockMvc.perform(get("/api/ecommerce/producto/{id}", producto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(5));
    }

    @Test
    void crearProductoSinTokenDevuelve401() throws Exception {
        mockMvc.perform(post("/api/ecommerce/producto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productoJson("Monitor", "150.00", 5)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crearProductoConRolUserDevuelve403() throws Exception {
        mockMvc.perform(post("/api/ecommerce/producto")
                        .header("Authorization", bearer(1L, "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productoJson("Monitor", "150.00", 5)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminPuedeCrearProducto() throws Exception {
        mockMvc.perform(post("/api/ecommerce/producto")
                        .header("Authorization", bearer(99L, "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productoJson("Monitor", "150.00", 5)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value("Monitor"));
    }

    @Test
    void elCarritoRequiereAutenticacion() throws Exception {
        mockMvc.perform(get("/api/ecommerce/carrito"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioAgregaProductoYRealizaCompraDescontandoStock() throws Exception {
        ProductoData producto = crearProducto("Teclado", "10.00", 5);
        simularUsuario(1L, "USER");

        mockMvc.perform(post("/api/ecommerce/carrito/agregar")
                        .header("Authorization", bearer(1L, "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(agregarItemJson(producto.getId(), 2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].cantidad").value(2))
                .andExpect(jsonPath("$.precioTotal").value(20.00));

        // El carrito no reserva stock.
        assertEquals(5, productoRepository.findById(producto.getId()).orElseThrow().getStock());

        mockMvc.perform(post("/api/ecommerce/compras/realizar")
                        .header("Authorization", bearer(1L, "USER")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(20.00))
                .andExpect(jsonPath("$.estado").value("COMPLETADA"));

        assertEquals(3, productoRepository.findById(producto.getId()).orElseThrow().getStock());
        assertEquals(1, compraRepository.count());
        assertEquals(0, carritoRepository.count());
        verify(notificacionGateway).enviarNotificacion(any());

        mockMvc.perform(get("/api/ecommerce/carrito")
                        .header("Authorization", bearer(1L, "USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void checkoutConCarritoVacioDevuelve400() throws Exception {
        simularUsuario(1L, "USER");

        mockMvc.perform(post("/api/ecommerce/compras/realizar")
                        .header("Authorization", bearer(1L, "USER")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "No hay items en el carrito para realizar la compra"));
    }

    @Test
    void checkoutSinStockNoCreaCompraNiDescuenta() throws Exception {
        ProductoData producto = crearProducto("Teclado", "10.00", 1);
        simularUsuario(1L, "USER");

        mockMvc.perform(post("/api/ecommerce/carrito/agregar")
                        .header("Authorization", bearer(1L, "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(agregarItemJson(producto.getId(), 1)))
                .andExpect(status().isOk());

        // Otro cliente compra la ultima unidad antes del checkout.
        producto.setStock(0);
        productoRepository.saveAndFlush(producto);

        mockMvc.perform(post("/api/ecommerce/compras/realizar")
                        .header("Authorization", bearer(1L, "USER")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Stock insuficiente para el producto Teclado"));

        assertEquals(0, compraRepository.count());
        assertEquals(0, productoRepository.findById(producto.getId()).orElseThrow().getStock());
        assertEquals(1, carritoRepository.count());
    }

    @Test
    void usuarioNoPuedeVerComprasDeOtroUsuario() throws Exception {
        mockMvc.perform(get("/api/ecommerce/compras/usuario/{id}", 2L)
                        .header("Authorization", bearer(1L, "USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminPuedeVerTodasLasCompras() throws Exception {
        ProductoData producto = crearProducto("Teclado", "10.00", 5);
        simularUsuario(1L, "USER");

        mockMvc.perform(post("/api/ecommerce/carrito/agregar")
                        .header("Authorization", bearer(1L, "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(agregarItemJson(producto.getId(), 1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/ecommerce/compras/realizar")
                        .header("Authorization", bearer(1L, "USER")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/ecommerce/compras")
                        .header("Authorization", bearer(99L, "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuarioId").value(1));
    }

    private ProductoData crearProducto(String nombre, String precio, int stock) {
        return productoRepository.save(new ProductoData(
                null, nombre, "descripcion", new BigDecimal(precio), stock, null));
    }

    private void simularUsuario(Long id, String role) {
        when(usuarioGateway.buscarPorId(id)).thenReturn(new Usuario(
                id, "Usuario " + id, "user" + id + "@example.com", "3001234567", role));
    }

    private String productoJson(String nombre, String precio, int stock) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "nombre", nombre,
                "descripcion", "descripcion",
                "precio", new BigDecimal(precio),
                "stock", stock));
    }

    private String agregarItemJson(Long productoId, int cantidad) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "productoId", productoId,
                "cantidad", cantidad));
    }

    private static String bearer(Long id, String role) {
        Instant now = Instant.now();
        String token = Jwts.builder()
                .subject(String.valueOf(id))
                .claim("email", "user" + id + "@example.com")
                .claim("nombre", "Usuario " + id)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3600)))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
        return "Bearer " + token;
    }
}
