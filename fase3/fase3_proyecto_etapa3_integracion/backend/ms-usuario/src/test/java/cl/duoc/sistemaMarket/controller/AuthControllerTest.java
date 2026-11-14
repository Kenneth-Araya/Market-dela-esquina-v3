package cl.duoc.sistemaMarket.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cl.duoc.sistemaMarket.dto.LoginRequest;
import cl.duoc.sistemaMarket.dto.LoginResponse;
import cl.duoc.sistemaMarket.exception.GlobalExceptionHandler;
import cl.duoc.sistemaMarket.exception.NoAutorizadoException;
import cl.duoc.sistemaMarket.model.TipoUsuario;
import cl.duoc.sistemaMarket.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private static final String JSON_LOGIN = """
            { "correoUsuario": "ana@duoc.cl", "contrasenaUsuario": "1234" }
            """;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/login correcto -> 200 con token y datos del usuario")
    void loginCorrecto() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new LoginResponse("token-jwt", 7L, "Ana", "ana@duoc.cl", TipoUsuario.CLIENTE));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_LOGIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt"))
                .andExpect(jsonPath("$.idUsuario").value(7))
                .andExpect(jsonPath("$.nombreUsuario").value("Ana"))
                .andExpect(jsonPath("$.correoUsuario").value("ana@duoc.cl"))
                .andExpect(jsonPath("$.tipoUsuario").value("CLIENTE"));

        ArgumentCaptor<LoginRequest> captor = ArgumentCaptor.forClass(LoginRequest.class);
        verify(authService).login(captor.capture());
        assertThat(captor.getValue().getCorreoUsuario()).isEqualTo("ana@duoc.cl");
        assertThat(captor.getValue().getContrasenaUsuario()).isEqualTo("1234");
    }

    @Test
    @DisplayName("POST /api/auth/login con credenciales incorrectas -> 401")
    void loginNoAutorizado() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new NoAutorizadoException("Correo o contraseña incorrectos."));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_LOGIN))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Correo o contraseña incorrectos."));
    }

    @Test
    @DisplayName("POST /api/auth/login con campos vacíos -> 400")
    void loginIncompleto() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("Por favor completa ambos campos."));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Por favor completa ambos campos."));
    }
}
