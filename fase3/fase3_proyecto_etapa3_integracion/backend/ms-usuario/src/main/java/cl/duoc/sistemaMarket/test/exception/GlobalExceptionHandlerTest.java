package cl.duoc.sistemaMarket.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.ResourceAccessException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private void verificar(ResponseEntity<Map<String, String>> respuesta, HttpStatus estado, String mensaje) {
        assertThat(respuesta.getStatusCode()).isEqualTo(estado);
        assertThat(respuesta.getBody()).containsOnly(Map.entry("mensaje", mensaje));
    }

    @Test
    @DisplayName("IllegalArgumentException -> 400 con el mensaje de la excepción")
    void solicitudInvalida() {
        verificar(handler.solicitudInvalida(new IllegalArgumentException("dato inválido")),
                HttpStatus.BAD_REQUEST, "dato inválido");
    }

    @Test
    @DisplayName("HttpMessageNotReadableException -> 400 con mensaje fijo")
    void cuerpoInvalido() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
                "JSON mal formado", mock(HttpInputMessage.class));

        verificar(handler.cuerpoInvalido(ex), HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud es inválido.");
    }

    @Test
    @DisplayName("NoSuchElementException -> 404")
    void noEncontrado() {
        verificar(handler.noEncontrado(new NoSuchElementException("Usuario no encontrado: 1")),
                HttpStatus.NOT_FOUND, "Usuario no encontrado: 1");
    }

    @Test
    @DisplayName("IllegalStateException -> 409")
    void conflicto() {
        verificar(handler.conflicto(new IllegalStateException("Ya existe")), HttpStatus.CONFLICT, "Ya existe");
    }

    @Test
    @DisplayName("ResourceAccessException -> 503 con mensaje fijo")
    void servicioNoDisponible() {
        verificar(handler.servicioNoDisponible(new ResourceAccessException("timeout")),
                HttpStatus.SERVICE_UNAVAILABLE, "Un servicio requerido no está disponible.");
    }

    @Test
    @DisplayName("NoAutorizadoException -> 401")
    void noAutorizado() {
        verificar(handler.noAutorizado(new NoAutorizadoException("Credenciales inválidas")),
                HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
    }

    @Test
    @DisplayName("una excepción sin mensaje responde con el texto genérico \"Error\"")
    void excepcionSinMensaje() {
        verificar(handler.solicitudInvalida(new IllegalArgumentException()), HttpStatus.BAD_REQUEST, "Error");
    }
}
