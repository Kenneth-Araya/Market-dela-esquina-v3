package cl.duoc.sistemaMarket.service;

import cl.duoc.sistemaMarket.client.ServiciosClient;
import cl.duoc.sistemaMarket.dto.DashboardDTO;
import cl.duoc.sistemaMarket.dto.EstadisticasUsuarioDTO;
import cl.duoc.sistemaMarket.dto.EstadisticasVentasDTO;
import cl.duoc.sistemaMarket.dto.ReporteGeneralDTO;
import cl.duoc.sistemaMarket.dto.UsuarioPorTipoDTO;
import cl.duoc.sistemaMarket.dto.VentaPorProductoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private ServiciosClient serviciosClient;

    @InjectMocks
    private ReporteService reporteService;

    private EstadisticasVentasDTO ventasDTOMock;
    private EstadisticasUsuarioDTO usuarioDTOMock;
    private VentaPorProductoDTO productoEstrellaMock;
    private VentaPorProductoDTO productoNormalMock;

    @BeforeEach
    void setUp() {
        ventasDTOMock = new EstadisticasVentasDTO();
        ventasDTOMock.setVentasTotales(100L);
        ventasDTOMock.setIngresosTotales(500000L);
        ventasDTOMock.setVentasMes(20L);
        ventasDTOMock.setIngresosMes(100000L);

        usuarioDTOMock = new EstadisticasUsuarioDTO();
        usuarioDTOMock.setTotalUsuarios(50L);
        usuarioDTOMock.setAdministradores(2L);
        usuarioDTOMock.setClientes(45L);
        usuarioDTOMock.setVendedores(3L);

        productoEstrellaMock = new VentaPorProductoDTO(1L, "Arroz", 50L, 50000L);
        productoNormalMock = new VentaPorProductoDTO(2L, "Fideos", 10L, 10000L);
    }

    @Test
    void general_RetornaReporteGeneralCompleto() {
        when(serviciosClient.estadisticasVentas()).thenReturn(ventasDTOMock);
        when(serviciosClient.estadisticasUsuarios()).thenReturn(usuarioDTOMock);
        when(serviciosClient.ventasPorProducto()).thenReturn(List.of(productoNormalMock, productoEstrellaMock));

        ReporteGeneralDTO resultado = reporteService.general();

        assertNotNull(resultado);
        assertEquals(500000L, resultado.getIngresosTotales());
        assertEquals(100L, resultado.getVentasTotales());
        assertEquals(50L, resultado.getUsuariosRegistrados());
        assertEquals("Arroz", resultado.getProductoEstrella());
    }

    @Test
    void general_SinProductosVendidos_RetornaEstrellaGuion() {
        when(serviciosClient.estadisticasVentas()).thenReturn(ventasDTOMock);
        when(serviciosClient.estadisticasUsuarios()).thenReturn(usuarioDTOMock);
        when(serviciosClient.ventasPorProducto()).thenReturn(List.of());

        ReporteGeneralDTO resultado = reporteService.general();

        assertEquals("-", resultado.getProductoEstrella());
    }

    @Test
    void ventasPorProducto_RetornaLista() {
        when(serviciosClient.ventasPorProducto()).thenReturn(List.of(productoEstrellaMock));

        List<VentaPorProductoDTO> resultado = reporteService.ventasPorProducto();

        assertFalse(resultado.isEmpty());
        assertEquals("Arroz", resultado.get(0).getNombreProducto());
    }

    @Test
    void usuariosPorTipo_RetornaListaFormateada() {
        when(serviciosClient.estadisticasUsuarios()).thenReturn(usuarioDTOMock);

        List<UsuarioPorTipoDTO> resultado = reporteService.usuariosPorTipo();

        assertEquals(3, resultado.size());
        assertEquals("ADMINISTRADOR", resultado.get(0).getTipoUsuario());
        assertEquals(2L, resultado.get(0).getCantidad());
        assertEquals("CLIENTE", resultado.get(1).getTipoUsuario());
        assertEquals(45L, resultado.get(1).getCantidad());
        assertEquals("VENDEDOR", resultado.get(2).getTipoUsuario());
        assertEquals(3L, resultado.get(2).getCantidad());
    }

    @Test
    void dashboard_RetornaMetricasDashboard() {
        when(serviciosClient.estadisticasVentas()).thenReturn(ventasDTOMock);
        when(serviciosClient.estadisticasUsuarios()).thenReturn(usuarioDTOMock);
        when(serviciosClient.totalProductos()).thenReturn(150L);

        DashboardDTO resultado = reporteService.dashboard();

        assertNotNull(resultado);
        assertEquals(150L, resultado.getProductos());
        assertEquals(50L, resultado.getUsuarios());
        assertEquals(20L, resultado.getVentasMes());
        assertEquals(100000L, resultado.getIngresosMes());
    }
}