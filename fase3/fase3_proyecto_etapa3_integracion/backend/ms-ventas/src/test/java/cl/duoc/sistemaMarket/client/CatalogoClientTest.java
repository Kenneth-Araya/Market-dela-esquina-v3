package cl.duoc.sistemaMarket.client;

import cl.duoc.sistemaMarket.dto.DescuentoRequest;
import cl.duoc.sistemaMarket.dto.ItemStockRequest;
import cl.duoc.sistemaMarket.dto.ProductoRemotoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogoClientTest {

    @Mock
    private RestTemplate restTemplate;

    private CatalogoClient catalogoClient;

    @BeforeEach
    void setUp() {
        catalogoClient = new CatalogoClient(restTemplate, new ObjectMapper());
        ReflectionTestUtils.setField(catalogoClient, "baseUrl", "http://localhost:8082");
    }

    @Test
    void obtenerProducto_Existe_RetornaDTO() {
        ProductoRemotoDTO dto = new ProductoRemotoDTO();
        dto.setId(1L);
        dto.setNombreProducto("Arroz");
        when(restTemplate.getForObject("http://localhost:8082/api/productos/1", ProductoRemotoDTO.class)).thenReturn(dto);

        ProductoRemotoDTO resultado = catalogoClient.obtenerProducto(1L);

        assertEquals("Arroz", resultado.getNombreProducto());
    }

    @Test
    void obtenerProducto_NotFound_LanzaNoSuchElement() {
        when(restTemplate.getForObject("http://localhost:8082/api/productos/99", ProductoRemotoDTO.class))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, new byte[0], StandardCharsets.UTF_8));

        assertThrows(NoSuchElementException.class, () -> catalogoClient.obtenerProducto(99L));
    }

    @Test
    void descontarYReponerStock_ExitosoYConErrores() {
        DescuentoRequest req = new DescuentoRequest(1L, List.of(new ItemStockRequest(1L, 2)));
        when(restTemplate.postForEntity("http://localhost:8082/api/inventario/descontar", req, Void.class))
                .thenReturn(ResponseEntity.ok().build());

        assertDoesNotThrow(() -> catalogoClient.descontarStock(req));

        byte[] bodyJson = "{\"mensaje\":\"Sin stock\"}".getBytes(StandardCharsets.UTF_8);
        when(restTemplate.postForEntity("http://localhost:8082/api/inventario/descontar", req, Void.class))
                .thenThrow(HttpClientErrorException.create(HttpStatus.CONFLICT, "Conflict", HttpHeaders.EMPTY, bodyJson, StandardCharsets.UTF_8));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> catalogoClient.descontarStock(req));
        assertEquals("Sin stock", ex.getMessage());

        when(restTemplate.postForEntity("http://localhost:8082/api/inventario/reponer", req, Void.class))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, new byte[0], StandardCharsets.UTF_8));

        assertThrows(NoSuchElementException.class, () -> catalogoClient.reponerStock(req));
    }
}