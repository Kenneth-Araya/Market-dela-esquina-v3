
package cl.duoc.sistemaMarket.config;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SeguridadConfig {

    // Define quién puede acceder a los endpoints
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Solicitudes de comprobación CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Documentación Swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**")
                        .permitAll()

                        // Permitir que los clientes envíen mensajes de contacto
                        .requestMatchers(HttpMethod.POST, "/api/contactos").permitAll()
                        
                        // Los reportes solo pueden consultarlos administradores
                        .requestMatchers("/api/reportes/**").hasRole("ADMINISTRADOR")

                        // Las demás rutas requieren autenticación
                        .anyRequest().authenticated())
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    // Verifica el JWT usando la misma clave de ms-usuario
    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.secret}") String secreto) {

        SecretKey clave = new SecretKeySpec(
                secreto.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256");

        return NimbusJwtDecoder.withSecretKey(clave)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    // Convierte ADMINISTRADOR en ROLE_ADMINISTRADOR
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();

        roles.setAuthoritiesClaimName("tipoUsuario");
        roles.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(roles);

        return converter;
    }
}
