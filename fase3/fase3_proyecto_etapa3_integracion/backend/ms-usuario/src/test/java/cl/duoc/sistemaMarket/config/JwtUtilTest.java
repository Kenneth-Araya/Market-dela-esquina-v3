package cl.duoc.sistemaMarket.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cl.duoc.sistemaMarket.model.TipoUsuario;
import cl.duoc.sistemaMarket.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;

class JwtUtilTest {

    private static final String SECRETO = "clave-secreta-solo-para-pruebas-unitarias-1234567890";
    private static final long EXPIRACION_MS = 60_000L;

    private final JwtUtil jwtUtil = new JwtUtil(SECRETO, EXPIRACION_MS);

    private Usuario usuario() {
        Usuario u = new Usuario();
        u.setId(42L);
        u.setCorreoUsuario("admin@duoc.cl");
        u.setTipoUsuario(TipoUsuario.ADMINISTRADOR);
        return u;
    }

    private Claims leer(String token, String secreto) {
        SecretKey clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
    }

    @Test
    @DisplayName("el token contiene el id como subject y los claims de correo y tipo de usuario")
    void tokenContieneLosDatosDelUsuario() {
        String token = jwtUtil.generarToken(usuario());

        Claims claims = leer(token, SECRETO);

        assertThat(claims.getSubject()).isEqualTo("42");
        assertThat(claims.get("correoUsuario", String.class)).isEqualTo("admin@duoc.cl");
        assertThat(claims.get("tipoUsuario", String.class)).isEqualTo("ADMINISTRADOR");
    }

    @Test
    @DisplayName("el token vence después del tiempo configurado")
    void tokenVenceSegunLaExpiracionConfigurada() {
        Claims claims = leer(jwtUtil.generarToken(usuario()), SECRETO);

        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration().getTime() - claims.getIssuedAt().getTime())
                .isEqualTo(EXPIRACION_MS);
    }

    @Test
    @DisplayName("un token ya vencido es rechazado al validarlo")
    void tokenVencidoEsRechazado() {
        JwtUtil conExpiracionNegativa = new JwtUtil(SECRETO, -5_000L);
        String token = conExpiracionNegativa.generarToken(usuario());

        assertThatThrownBy(() -> leer(token, SECRETO)).isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("un token firmado con otra clave es rechazado")
    void tokenConOtraClaveEsRechazado() {
        String token = jwtUtil.generarToken(usuario());

        assertThatThrownBy(() -> leer(token, "otra-clave-distinta-para-probar-la-firma-0987654321"))
                .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("un secreto demasiado corto para HS256 hace fallar la creación")
    void secretoDemasiadoCorto() {
        assertThatThrownBy(() -> new JwtUtil("corto", EXPIRACION_MS)).isInstanceOf(WeakKeyException.class);
    }
}
