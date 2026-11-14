
package cl.duoc.sistemaMarket.config;

import java.nio.charset.StandardCharsets;
import java.util.List;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SeguridadConfig {

    // 1. Conservamos BCrypt para las contraseñas.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Definimos qué endpoints puede utilizar cada usuario.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                // API REST con autenticación JWT, sin sesiones de servidor.
                .csrf(csrf -> csrf.disable())

                // Permitimos solicitudes del frontend autorizado.
                .cors(Customizer.withDefaults())

                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // Permitir solicitudes CORS de comprobación.
                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        // Login público.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/login")
                        .permitAll()

                        // Registro público: el backend asigna CLIENTE.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/usuarios")
                        .permitAll()

                        // Documentación Swagger.
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**")
                        .permitAll()

                        // Gestión de usuarios: solo administradores.
                        .requestMatchers(
                                "/api/usuarios",
                                "/api/usuarios/**")
                        .hasRole("ADMINISTRADOR")

                        // Cualquier otra ruta requiere autenticación.
                        .anyRequest()
                        .authenticated())

                // Validación automática de tokens Bearer JWT.
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(
                        jwtAuthenticationConverter())));

        return http.build();
    }

    // 3. Validar JWT con la misma clave de JwtUtil.
    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${jwt.secret}") String secreto) {

        SecretKey clave = new SecretKeySpec(
                secreto.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256");

        return NimbusJwtDecoder
                .withSecretKey(clave)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    // 4. Convertir el rol guardado en el token.
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();

        // El token contiene "tipoUsuario": "ADMINISTRADOR".
        roles.setAuthoritiesClaimName("tipoUsuario");

        // Spring Security lo reconocerá como ROLE_ADMINISTRADOR.
        roles.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(roles);

        return converter;
    }

    // 5. Configuración CORS para desarrollo local.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // Frontends locales, incluyendo Vite.
        config.setAllowedOriginPatterns(
                List.of(
                        "http://localhost:*",
                        "http://127.0.0.1:*"));

        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        config.setAllowedHeaders(
                List.of("Authorization", "Content-Type", "Accept"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
