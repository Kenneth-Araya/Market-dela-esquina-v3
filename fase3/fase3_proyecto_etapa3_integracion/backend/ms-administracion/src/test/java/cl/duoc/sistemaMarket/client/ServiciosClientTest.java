package cl.duoc.sistemaMarket.client;

import cl.duoc.sistemaMarket.dto.EstadisticasUsuarioDTO;
import cl.duoc.sistemaMarket.dto.EstadisticasVentasDTO;
import cl.duoc.sistemaMarket.dto.VentaPorProductoDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiciosClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ServiciosClient serviciosClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(serviciosClient, "usuarioUrl", "http://localhost:8081");
        ReflectionTestUtils.setField(serviciosClient, "catalogoUrl", "http://localhost:8082");
        ReflectionTestUtils.setField(serviciosClient, "ventasUrl", "http://localhost:8083");
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void estadisticasUsuarios_ConToken_EnviaHeaderYRetornaDTO() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-prueba");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        EstadisticasUsuarioDTO dto = new EstadisticasUsuarioDTO(10L, 1L, 8L, 1L);
        when(restTemplate.exchange(
                eq("http://localhost:8081/api/usuarios/estadisticas"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(EstadisticasUsuarioDTO.class)
        )).thenReturn(ResponseEntity.ok(dto));

        EstadisticasUsuarioDTO resultado = serviciosClient.estadisticasUsuarios();

        assertNotNull(resultado);
        assertEquals(10L, resultado.getTotalUsuarios());
    }

    @Test
    void totalProductos_RetornaValor() {
        when(restTemplate.getForObject("http://localhost:8082/api/productos/total", Long.class)).thenReturn(25L);

        long total = serviciosClient.totalProductos();

        assertEquals(25L, total);
    }

    @Test
    void totalProductos_Nulo_RetornaCero() {
        when(restTemplate.getForObject("http://localhost:8082/api/productos/total", Long.class)).thenReturn(null);

        long total = serviciosClient.totalProductos();

        assertEquals(0L, total);
    }

    @Test
    void estadisticasVentas_RetornaDTO() {
        EstadisticasVentasDTO dto = new EstadisticasVentasDTO();
        dto.setVentasTotales(40L);
        when(restTemplate.getForObject("http://localhost:8083/api/ventas/estadisticas", EstadisticasVentasDTO.class)).thenReturn(dto);

        EstadisticasVentasDTO resultado = serviciosClient.estadisticasVentas();

        assertNotNull(resultado);
        assertEquals(40L, resultado.getVentasTotales());
    }

    @Test
    void ventasPorProducto_ConDatos_RetornaLista() {
        VentaPorProductoDTO[] arreglo = { new VentaPorProductoDTO(1L, "Arroz", 15L, 22500L) };
        when(restTemplate.getForObject("http://localhost:8083/api/ventas/reportes/por-producto", VentaPorProductoDTO[].class))
                .thenReturn(arreglo);

        List<VentaPorProductoDTO> resultado = serviciosClient.ventasPorProducto();

        assertEquals(1, resultado.size());
        assertEquals("Arroz", resultado.get(0).getNombreProducto());
    }

    @Test
    void ventasPorProducto_Nulo_RetornaListaVacia() {
        when(restTemplate.getForObject("http://localhost:8083/api/ventas/reportes/por-producto", VentaPorProductoDTO[].class))
                .thenReturn(null);

        List<VentaPorProductoDTO> resultado = serviciosClient.ventasPorProducto();

        assertTrue(resultado.isEmpty());
    }
}