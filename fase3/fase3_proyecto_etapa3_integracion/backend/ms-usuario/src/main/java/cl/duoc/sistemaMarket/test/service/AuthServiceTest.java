package cl.duoc.sistemaMarket.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import cl.duoc.sistemaMarket.config.JwtUtil;
import cl.duoc.sistemaMarket.dto.LoginRequest;
import cl.duoc.sistemaMarket.dto.LoginResponse;
import cl.duoc.sistemaMarket.exception.NoAutorizadoException;
import cl.duoc.sistemaMarket.model.EstadoUsuario;
import cl.duoc.sistemaMarket.model.TipoUsuario;
import cl.duoc.sistemaMarket.model.Usuario;
import cl.duoc.sistemaMarket.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String MENSAJE_CREDENCIALES = "Correo o contraseña incorrectos.";

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioActivo() {
        Usuario u = new Usuario();
        u.setId(7L);
        u.setNombreUsuario("Ana");
        u.setCorreoUsuario("ana@duoc.cl");
        u.setContrasenaUsuario("hash-bcrypt");
        u.setTipoUsuario(TipoUsuario.CLIENTE);
        u.setEstadoUsuario(EstadoUsuario.ACTIVO);
        return u;
    }

    private LoginRequest request(String correo, String clave) {
        LoginRequest r = new LoginRequest();
        r.setCorreoUsuario(correo);
        r.setContrasenaUsuario(clave);
        return r;
    }

    static Stream<Arguments> credencialesIncompletas() {
        return Stream.of(
                Arguments.of(null, "1234"),
                Arguments.of("", "1234"),
                Arguments.of("   ", "1234"),
                Arguments.of("ana@duoc.cl", null),
                Arguments.of("ana@duoc.cl", ""),
                Arguments.of("ana@duoc.cl", "   "),
                Arguments.of(null, null));
    }

    @Test
    @DisplayName("login correcto devuelve token y datos del usuario")
    void loginCorrecto() {
        Usuario usuario = usuarioActivo();
        when(usuarioRepository.findByCorreoUsuario("ana@duoc.cl")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("1234", "hash-bcrypt")).thenReturn(true);
        when(jwtUtil.generarToken(usuario)).thenReturn("token-jwt");

        LoginResponse respuesta = authService.login(request("ana@duoc.cl", "1234"));

        assertThat(respuesta.getToken()).isEqualTo("token-jwt");
        assertThat(respuesta.getIdUsuario()).isEqualTo(7L);
        assertThat(respuesta.getNombreUsuario()).isEqualTo("Ana");
        assertThat(respuesta.getCorreoUsuario()).isEqualTo("ana@duoc.cl");
        assertThat(respuesta.getTipoUsuario()).isEqualTo(TipoUsuario.CLIENTE);
    }

    @Test
    @DisplayName("login normaliza el correo (espacios y mayúsculas) antes de buscarlo")
    void loginNormalizaCorreo() {
        Usuario usuario = usuarioActivo();
        when(usuarioRepository.findByCorreoUsuario("ana@duoc.cl")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("1234", "hash-bcrypt")).thenReturn(true);
        when(jwtUtil.generarToken(usuario)).thenReturn("token-jwt");

        LoginResponse respuesta = authService.login(request("  ANA@Duoc.CL  ", "1234"));

        assertThat(respuesta.getToken()).isEqualTo("token-jwt");
        verify(usuarioRepository).findByCorreoUsuario("ana@duoc.cl");
    }

    @ParameterizedTest(name = "correo=[{0}] clave=[{1}]")
    @MethodSource("credencialesIncompletas")
    @DisplayName("login con campos vacíos o nulos lanza IllegalArgumentException")
    void loginConCamposIncompletos(String correo, String clave) {
        assertThatThrownBy(() -> authService.login(request(correo, clave)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Por favor completa ambos campos.");

        verifyNoInteractions(usuarioRepository, passwordEncoder, jwtUtil);
    }

    @Test
    @DisplayName("login con correo inexistente lanza NoAutorizadoException")
    void loginCorreoInexistente() {
        when(usuarioRepository.findByCorreoUsuario("nadie@duoc.cl")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request("nadie@duoc.cl", "1234")))
                .isInstanceOf(NoAutorizadoException.class)
                .hasMessage(MENSAJE_CREDENCIALES);

        verifyNoInteractions(passwordEncoder, jwtUtil);
    }

    @Test
    @DisplayName("login con contraseña incorrecta lanza NoAutorizadoException y no genera token")
    void loginContrasenaIncorrecta() {
        Usuario usuario = usuarioActivo();
        when(usuarioRepository.findByCorreoUsuario("ana@duoc.cl")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("mala", "hash-bcrypt")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request("ana@duoc.cl", "mala")))
                .isInstanceOf(NoAutorizadoException.class)
                .hasMessage(MENSAJE_CREDENCIALES);

        verify(jwtUtil, never()).generarToken(any(Usuario.class));
    }

    @Test
    @DisplayName("login de usuario inactivo con contraseña correcta lanza NoAutorizadoException")
    void loginUsuarioInactivo() {
        Usuario usuario = usuarioActivo();
        usuario.setEstadoUsuario(EstadoUsuario.INACTIVO);
        when(usuarioRepository.findByCorreoUsuario("ana@duoc.cl")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("1234", "hash-bcrypt")).thenReturn(true);

        assertThatThrownBy(() -> authService.login(request("ana@duoc.cl", "1234")))
                .isInstanceOf(NoAutorizadoException.class)
                .hasMessage("El usuario está inactivo.");

        verify(jwtUtil, never()).generarToken(any(Usuario.class));
    }

    @Test
    @DisplayName("login de usuario inactivo con contraseña incorrecta no revela que está inactivo")
    void loginInactivoConContrasenaIncorrectaNoRevelaEstado() {
        Usuario usuario = usuarioActivo();
        usuario.setEstadoUsuario(EstadoUsuario.INACTIVO);
        when(usuarioRepository.findByCorreoUsuario("ana@duoc.cl")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("mala", "hash-bcrypt")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request("ana@duoc.cl", "mala")))
                .isInstanceOf(NoAutorizadoException.class)
                .hasMessage(MENSAJE_CREDENCIALES);
    }
}
