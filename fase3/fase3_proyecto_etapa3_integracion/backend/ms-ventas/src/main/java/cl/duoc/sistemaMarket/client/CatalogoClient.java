package cl.duoc.sistemaMarket.client;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.sistemaMarket.dto.DescuentoRequest;
import cl.duoc.sistemaMarket.dto.ProductoRemotoDTO;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CatalogoClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${catalogo.url}")
    private String baseUrl;

    public ProductoRemotoDTO obtenerProducto(Long idProducto) {
        try {
            return restTemplate.getForObject(baseUrl + "/api/productos/" + idProducto, ProductoRemotoDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new NoSuchElementException("Producto no encontrado: " + idProducto);
        }
    }

    /** Descuenta el stock; si no alcanza lanza IllegalStateException con el detalle. */
    public void descontarStock(DescuentoRequest request) {
        enviar("/api/inventario/descontar", request);
    }

    public void reponerStock(DescuentoRequest request) {
        enviar("/api/inventario/reponer", request);
    }

    private void enviar(String ruta, DescuentoRequest request) {
        try {
            restTemplate.postForEntity(baseUrl + ruta, request, Void.class);
        } catch (HttpClientErrorException.Conflict e) {
            throw new IllegalStateException(extraerMensaje(e));
        } catch (HttpClientErrorException.NotFound e) {
            throw new NoSuchElementException(extraerMensaje(e));
        }
    }

    private String extraerMensaje(HttpClientErrorException e) {
        try {
            return objectMapper.readTree(e.getResponseBodyAsString()).path("mensaje").asText("Error en el catálogo.");
        } catch (Exception ex) {
            return "Error en el catálogo.";
        }
    }
}
