package cl.duoc.sistemaMarket.exception;

import cl.duoc.sistemaMarket.config.RestTemplateConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.ResourceAccessException;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void restTemplateConfig_CreaInstancia() {
        RestTemplateConfig config = new RestTemplateConfig();
        assertNotNull(config.restTemplate(new RestTemplateBuilder()));
    }

    @Test
    void manejadoresExcepciones_RetornanCodigosHttpCorrectos() {
        assertEquals(HttpStatus.BAD_REQUEST, handler.solicitudInvalida(new IllegalArgumentException("error")).getStatusCode());

        @SuppressWarnings("deprecation")
        var r2 = handler.cuerpoInvalido(new HttpMessageNotReadableException("mal formado"));
        assertEquals(HttpStatus.BAD_REQUEST, r2.getStatusCode());

        assertEquals(HttpStatus.NOT_FOUND, handler.noEncontrado(new NoSuchElementException("no existe")).getStatusCode());
        assertEquals(HttpStatus.CONFLICT, handler.conflicto(new IllegalStateException("conflicto")).getStatusCode());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, handler.servicioNoDisponible(new ResourceAccessException("caido")).getStatusCode());
    }
}