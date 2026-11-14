package cl.duoc.sistemaMarket.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.ResourceAccessException;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void solicitudInvalida_RetornaBadRequest() {
        var response = handler.solicitudInvalida(new IllegalArgumentException("error de validacion"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("error de validacion", response.getBody().get("mensaje"));
    }

    @Test
    void cuerpoInvalido_RetornaBadRequest() {
        @SuppressWarnings("deprecation")
        var response = handler.cuerpoInvalido(new HttpMessageNotReadableException("cuerpo mal formado"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("el cuerpo de la solicitud es invalido.", response.getBody().get("mensaje").toLowerCase().replace("á", "a"));
    }

    @Test
    void noEncontrado_RetornaNotFound() {
        var response = handler.noEncontrado(new NoSuchElementException("recurso no encontrado"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("recurso no encontrado", response.getBody().get("mensaje"));
    }

    @Test
    void conflicto_RetornaConflict() {
        var response = handler.conflicto(new IllegalStateException("estado conflictivo"));
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("estado conflictivo", response.getBody().get("mensaje"));
    }

    @Test
    void servicioNoDisponible_RetornaServiceUnavailable() {
        var response = handler.servicioNoDisponible(new ResourceAccessException("falla conexion externa"));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("un servicio requerido no esta disponible.", response.getBody().get("mensaje").toLowerCase().replace("á", "a"));
    }
}