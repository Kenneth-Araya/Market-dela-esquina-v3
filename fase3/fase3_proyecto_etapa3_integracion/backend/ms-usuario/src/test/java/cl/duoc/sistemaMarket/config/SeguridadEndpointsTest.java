package cl.duoc.sistemaMarket.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import cl.duoc.sistemaMarket.controller.AuthController;
import cl.duoc.sistemaMarket.controller.UbicacionController;
import cl.duoc.sistemaMarket.controller.UsuarioController;
import cl.duoc.sistemaMarket.model.TipoUsuario;
import cl.duoc.sistemaMarket.model.Usuario;
import cl.duoc.sistemaMarket.service.AuthService;
import cl.duoc.sistemaMarket.service.UbicacionService;
import cl.duoc.sistemaMarket.service.UsuarioService;

@WebMvcTest(controllers = { UsuarioController.class, AuthController.class, UbicacionController.class })
@Import(SeguridadConfig.class)
@TestPropertySource(properties = "jwt.secret=" + SeguridadEndpointsTest.SECRETO)
class SeguridadEndpointsTest {

    static final String SECRETO = "clave-secreta-solo-para-pruebas-de-seguridad-1234567890";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UbicacionService ubicacionService;

    private String bearer(TipoUsuario tipo) {
        return "Bearer " + token(SECRETO, tipo);
    }

    private String token(String secreto, TipoUsuario tipo) {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setCorreoUsuario("prueba@duoc.cl");
        u.setTipoUsuario(tipo);
        return new JwtUtil(secreto, 3_600_000L).generarToken(u);
    }

    //rutas públicas

    @Test
    @DisplayName("POST /api/auth/login es público")
    void loginEsPublico() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"correoUsuario\":\"a@duoc.cl\",\"contrasenaUsuario\":\"1234\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/usuarios (registro) es público")
    void registroEsPublico() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("las rutas de Swagger no exigen autenticación")
    void swaggerEsPublico() throws Exception {
        mockMvc.perform(get("/v3/api-docs")).andExpect(result -> assertThat(result.getResponse().getStatus())
                .isNotIn(401, 403));
    }

    //rutas de administrador 
    @Test
    @DisplayName("GET /api/usuarios sin token -> 401")
    void listarSinTokenEs401() throws Exception {
        mockMvc.perform(get("/api/usuarios")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/usuarios con token de CLIENTE -> 403")
    void listarComoClienteEs403() throws Exception {
        mockMvc.perform(get("/api/usuarios").header(HttpHeaders.AUTHORIZATION, bearer(TipoUsuario.CLIENTE)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/usuarios con token de VENDEDOR -> 403")
    void listarComoVendedorEs403() throws Exception {
        mockMvc.perform(get("/api/usuarios").header(HttpHeaders.AUTHORIZATION, bearer(TipoUsuario.VENDEDOR)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/usuarios con token de ADMINISTRADOR -> 200")
    void listarComoAdminEs200() throws Exception {
        mockMvc.perform(get("/api/usuarios").header(HttpHeaders.AUTHORIZATION, bearer(TipoUsuario.ADMINISTRADOR)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/usuarios/{id} con token de ADMINISTRADOR -> 204")
    void eliminarComoAdminEs204() throws Exception {
        mockMvc.perform(delete("/api/usuarios/1").header(HttpHeaders.AUTHORIZATION, bearer(TipoUsuario.ADMINISTRADOR)))
                .andExpect(status().isNoContent());

        verify(usuarioService).eliminar(1L);
    }

    @Test
    @DisplayName("DELETE /api/usuarios/{id} con token de CLIENTE -> 403")
    void eliminarComoClienteEs403() throws Exception {
        mockMvc.perform(delete("/api/usuarios/1").header(HttpHeaders.AUTHORIZATION, bearer(TipoUsuario.CLIENTE)))
                .andExpect(status().isForbidden());
    }

    //otras rutas: basta con estar autenticado
    @Test
    @DisplayName("GET /api/regiones sin token -> 401")
    void regionesSinTokenEs401() throws Exception {
        mockMvc.perform(get("/api/regiones")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/regiones con token de CLIENTE -> 200")
    void regionesConTokenEs200() throws Exception {
        mockMvc.perform(get("/api/regiones").header(HttpHeaders.AUTHORIZATION, bearer(TipoUsuario.CLIENTE)))
                .andExpect(status().isOk());
    }

    //tokens inválidos

    @Test
    @DisplayName("un token firmado con otra clave -> 401")
    void tokenConOtraClaveEs401() throws Exception {
        String falso = "Bearer " + token("otra-clave-distinta-para-un-token-falso-0987654321", TipoUsuario.ADMINISTRADOR);

        mockMvc.perform(get("/api/usuarios").header(HttpHeaders.AUTHORIZATION, falso))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("un token que no es JWT -> 401")
    void tokenMalFormadoEs401() throws Exception {
        mockMvc.perform(get("/api/usuarios").header(HttpHeaders.AUTHORIZATION, "Bearer esto-no-es-un-jwt"))
                .andExpect(status().isUnauthorized());
    }

    //CORS

    @Test
    @DisplayName("el preflight CORS desde localhost (Vite) es aceptado")
    void preflightDesdeLocalhostEsAceptado() throws Exception {
        mockMvc.perform(options("/api/usuarios")
                .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"));
    }

    @Test
    @DisplayName("el preflight CORS desde otro origen es rechazado")
    void preflightDesdeOtroOrigenEsRechazado() throws Exception {
        mockMvc.perform(options("/api/usuarios")
                .header(HttpHeaders.ORIGIN, "http://sitio-malicioso.com")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isForbidden());
    }
}
