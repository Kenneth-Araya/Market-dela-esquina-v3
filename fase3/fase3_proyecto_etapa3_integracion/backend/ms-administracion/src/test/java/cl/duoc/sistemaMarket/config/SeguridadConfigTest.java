package cl.duoc.sistemaMarket.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SeguridadConfigTest {

    private final SeguridadConfig seguridadConfig = new SeguridadConfig();
    private final RestTemplateConfig restTemplateConfig = new RestTemplateConfig();

    @Test
    void restTemplate_CreaInstancia() {
        RestTemplate rt = restTemplateConfig.restTemplate();
        assertNotNull(rt);
    }

    @Test
    void jwtDecoder_CreaInstanciaConSecretoValido() {
        JwtDecoder decoder = seguridadConfig.jwtDecoder("claveSecretaSuperSeguraDeAlMenos32Caracteres1234");
        assertNotNull(decoder);
    }

    @Test
    void jwtAuthenticationConverter_ConvierteRolConPrefijo() {
        JwtAuthenticationConverter converter = seguridadConfig.jwtAuthenticationConverter();
        Jwt jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "HS256"),
                Map.of("sub", "admin@market.cl", "tipoUsuario", "ADMINISTRADOR")
        );

        var auth = converter.convert(jwt);
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR")));
    }
}