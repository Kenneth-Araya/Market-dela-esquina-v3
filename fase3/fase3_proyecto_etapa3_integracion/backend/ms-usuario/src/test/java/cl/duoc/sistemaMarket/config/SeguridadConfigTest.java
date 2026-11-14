package cl.duoc.sistemaMarket.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import cl.duoc.sistemaMarket.model.TipoUsuario;
import cl.duoc.sistemaMarket.model.Usuario;

/**
 * Pruebas unitarias de los beans de SeguridadConfig (sin levantar Spring).
 * Las reglas de acceso a cada endpoint se prueban en SeguridadEndpointsTest.
 */
class SeguridadConfigTest {

    private static final String SECRETO = "clave-secreta-solo-para-pruebas-unitarias-1234567890";

    private final SeguridadConfig config = new SeguridadConfig();

    @Test
    @DisplayName("passwordEncoder usa BCrypt: cifra y verifica contraseñas")
    void passwordEncoder() {
        PasswordEncoder encoder = config.passwordEncoder();

        String hash = encoder.encode("1234");

        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
        assertThat(hash).isNotEqualTo("1234");
        assertThat(encoder.matches("1234", hash)).isTrue();
        assertThat(encoder.matches("otra", hash)).isFalse();
    }

    @Test
    @DisplayName("jwtAuthenticationConverter convierte tipoUsuario en ROLE_<tipo>")
    void converterConvierteTipoUsuarioEnRol() {
        JwtAuthenticationConverter converter = config.jwtAuthenticationConverter();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .claim("tipoUsuario", "ADMINISTRADOR")
                .build();

        AbstractAuthenticationToken autenticacion = converter.convert(jwt);

        assertThat(autenticacion).isNotNull();
        assertThat(autenticacion.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMINISTRADOR");
    }

    @Test
    @DisplayName("jwtDecoder acepta tokens firmados con el mismo secreto que JwtUtil")
    void decoderAceptaTokenValido() {
        JwtDecoder decoder = config.jwtDecoder(SECRETO);
        String token = new JwtUtil(SECRETO, 60_000L).generarToken(usuario());

        Jwt jwt = decoder.decode(token);

        assertThat(jwt.getSubject()).isEqualTo("3");
        assertThat(jwt.getClaimAsString("tipoUsuario")).isEqualTo("CLIENTE");
        assertThat(jwt.getClaimAsString("correoUsuario")).isEqualTo("ana@duoc.cl");
    }

    @Test
    @DisplayName("jwtDecoder rechaza tokens firmados con otro secreto")
    void decoderRechazaTokenConOtroSecreto() {
        JwtDecoder decoder = config.jwtDecoder(SECRETO);
        String token = new JwtUtil("otro-secreto-distinto-para-probar-el-rechazo-9876543210", 60_000L)
                .generarToken(usuario());

        assertThatThrownBy(() -> decoder.decode(token)).isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("jwtDecoder rechaza tokens vencidos")
    void decoderRechazaTokenVencido() {
        JwtDecoder decoder = config.jwtDecoder(SECRETO);
        // Spring tolera 60 s de desfase de reloj, así que lo vencemos por 5 minutos.
        String token = new JwtUtil(SECRETO, -300_000L).generarToken(usuario());

        assertThatThrownBy(() -> decoder.decode(token)).isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("CORS permite los frontends locales con los métodos y cabeceras esperados")
    void corsPermiteFrontendsLocales() {
        CorsConfigurationSource fuente = config.corsConfigurationSource();

        assertThat(fuente).isInstanceOf(UrlBasedCorsConfigurationSource.class);
        CorsConfiguration cors = ((UrlBasedCorsConfigurationSource) fuente).getCorsConfigurations().get("/**");

        assertThat(cors).isNotNull();
        assertThat(cors.getAllowedOriginPatterns())
                .containsExactly("http://localhost:*", "http://127.0.0.1:*");
        assertThat(cors.getAllowedMethods())
                .containsExactly("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
        assertThat(cors.getAllowedHeaders()).containsExactly("Authorization", "Content-Type", "Accept");
    }

    private Usuario usuario() {
        Usuario u = new Usuario();
        u.setId(3L);
        u.setCorreoUsuario("ana@duoc.cl");
        u.setTipoUsuario(TipoUsuario.CLIENTE);
        return u;
    }
}
