package cl.duoc.sistemaMarket.Service;
 
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import cl.duoc.msVenta.Client.BoletaClient;
import cl.duoc.msVenta.dto.BoletaVentaDTO;
import cl.duoc.msVenta.dto.VentaDTO;
import cl.duoc.msVenta.model.Venta;
import cl.duoc.msVenta.repository.VentaRepository;
import cl.duoc.msVenta.service.VentaService;
 
@ExtendWith(MockitoExtension.class)
public class VentaServiceTest {
 
    @Mock
    private VentaRepository ventaRepository;
 
    @Mock
    private BoletaClient boletaClient;
 
    @InjectMocks
    private VentaService ventaService;
 
    private VentaDTO buildVentaDTO(String descripcion) {
        VentaDTO dto = new VentaDTO();
        dto.setDescripcionVentaDto(descripcion);
        dto.setCodigoTransaccionVentaDto("TXN-001");
        dto.setProductos(List.of("PROD-001"));
        dto.setMontoPagoVentaDto(10000.0);
        return dto;
    }
 
    private BoletaVentaDTO buildBoletaDTO(String folio) {
        BoletaVentaDTO dto = new BoletaVentaDTO();
        dto.setFolio(folio);
        dto.setEstado("PENDIENTE");
        dto.setFecha(LocalDate.now());
        return dto;
    }
 
    @Test
    void listarTodos_RepositorioVacio_DeberiaRetornarListaVacia() {
        when(ventaRepository.findAll()).thenReturn(Collections.emptyList());
 
        List<VentaDTO> resultado = ventaService.listarTodos();
 
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(ventaRepository, times(1)).findAll();
    }
 
    @Test
    void guardarVenta_ProductosListaVacia_DeberiaLanzarIllegalArgumentException() {
        VentaDTO dto = new VentaDTO();
        dto.setCodigoTransaccionVentaDto("TXN-001");
        dto.setProductos(Collections.emptyList()); 
 
        assertThrows(IllegalArgumentException.class, () -> ventaService.guardarVenta(dto));
        verify(ventaRepository, never()).save(any());
    }
 
    @Test
    void guardarVenta_CodigoTransaccionNulo_DeberiaLanzarIllegalArgumentException() {
        VentaDTO dto = new VentaDTO();
        dto.setProductos(List.of("PROD-001"));
        dto.setCodigoTransaccionVentaDto(null); 
 
        assertThrows(IllegalArgumentException.class, () -> ventaService.guardarVenta(dto));
        verify(ventaRepository, never()).save(any());
    }
 
    @Test
    void guardarVenta_CodigoTransaccionVacio_DeberiaLanzarIllegalArgumentException() {
        VentaDTO dto = new VentaDTO();
        dto.setProductos(List.of("PROD-001"));
        dto.setCodigoTransaccionVentaDto(""); 
 
        assertThrows(IllegalArgumentException.class, () -> ventaService.guardarVenta(dto));
        verify(ventaRepository, never()).save(any());
    }
 

    @Test
    void findById_IdNoExiste_DeberiaLanzarRuntimeException() {
        when(ventaRepository.findById(999L)).thenReturn(Optional.empty());
 
        assertThrows(RuntimeException.class, () -> ventaService.findById(999L));
        verify(ventaRepository, times(1)).findById(999L);
    }
 

    @Test
    void actualizarVenta_CodigoTransaccionNulo_DeberiaLanzarIllegalArgumentException() {
        VentaDTO dto = new VentaDTO();
        dto.setCodigoTransaccionVentaDto(null);
        dto.setProductos(List.of("PROD-001"));
 
        assertThrows(IllegalArgumentException.class,
                () -> ventaService.actualizarVenta(1L, dto));
        verify(ventaRepository, never()).save(any());
    }
 
    @Test
    void actualizarVenta_CodigoTransaccionVacio_DeberiaLanzarIllegalArgumentException() {
        VentaDTO dto = new VentaDTO();
        dto.setCodigoTransaccionVentaDto("");
        dto.setProductos(List.of("PROD-001"));
 
        assertThrows(IllegalArgumentException.class,
                () -> ventaService.actualizarVenta(1L, dto));
        verify(ventaRepository, never()).save(any());
    }
 
    @Test
    void actualizarVenta_SinProductos_DeberiaLanzarIllegalArgumentException() {
        VentaDTO dto = new VentaDTO();
        dto.setCodigoTransaccionVentaDto("TXN-001");
        dto.setDescripcionVentaDto("Descripcion unica");
        dto.setProductos(Collections.emptyList());
 
        when(ventaRepository.existsByDescripcionVenta("Descripcion unica")).thenReturn(false);
 
        assertThrows(IllegalArgumentException.class,
                () -> ventaService.actualizarVenta(1L, dto));
        verify(ventaRepository, never()).save(any());
    }
 
    @Test
    void actualizarVenta_DescripcionDuplicada_DeberiaLanzarIllegalArgumentException() {
        VentaDTO dto = buildVentaDTO("Descripcion ya existente");
 
        when(ventaRepository.existsByDescripcionVenta("Descripcion ya existente")).thenReturn(true);
 
        assertThrows(IllegalArgumentException.class,
                () -> ventaService.actualizarVenta(1L, dto));
        verify(ventaRepository, never()).findByIdVenta(anyLong());
        verify(ventaRepository, never()).save(any());
    }

    @Test
    void eliminarPorId_Exitoso_DeberiaRetornarTrue() {
        Venta venta = new Venta();
        venta.setIdVenta(1L);
        when(ventaRepository.findByIdVenta(1L)).thenReturn(Optional.of(venta));
 
        boolean resultado = ventaService.eliminarPorId(1L);
 
        assertTrue(resultado);
        verify(ventaRepository, times(1)).deleteByIdVenta(1L);
    }
 
 
    @Test
    void listarBoletas_DeberiaRetornarListaDesdeFeignClient() {
        List<BoletaVentaDTO> boletas = List.of(buildBoletaDTO("BOL-001"), buildBoletaDTO("BOL-002"));
        when(boletaClient.listarBoletas()).thenReturn(boletas);
 
        List<BoletaVentaDTO> resultado = ventaService.listarBoletas();
 
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(boletaClient, times(1)).listarBoletas();
    }
 
    @Test
    void listarBoletas_ListaVacia_DeberiaRetornarListaVacia() {
        when(boletaClient.listarBoletas()).thenReturn(Collections.emptyList());
 
        List<BoletaVentaDTO> resultado = ventaService.listarBoletas();
 
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerBoletaPorFolio_FolioValido_DeberiaRetornarBoleta() {
        BoletaVentaDTO boleta = buildBoletaDTO("BOL-001");
        when(boletaClient.obtenerBoletaPorFolio("BOL-001")).thenReturn(boleta);
 
        BoletaVentaDTO resultado = ventaService.obtenerBoletaPorFolio("BOL-001");
 
        assertNotNull(resultado);
        assertEquals("BOL-001", resultado.getFolio());
        verify(boletaClient, times(1)).obtenerBoletaPorFolio("BOL-001");
    }
 
    @Test
    void obtenerBoletaPorFolio_FolioNulo_DeberiaLanzarIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> ventaService.obtenerBoletaPorFolio(null));
        verify(boletaClient, never()).obtenerBoletaPorFolio(anyString());
    }
 
    @Test
    void obtenerBoletaPorFolio_FolioVacio_DeberiaLanzarIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> ventaService.obtenerBoletaPorFolio("   "));
        verify(boletaClient, never()).obtenerBoletaPorFolio(anyString());
    }

    @Test
    void crearBoletaParaVenta_Exitosa_DeberiaRetornarBoletaCreada() {
        BoletaVentaDTO dto = buildBoletaDTO("BOL-001");
        when(boletaClient.crearBoleta(dto)).thenReturn(dto);
 
        BoletaVentaDTO resultado = ventaService.crearBoletaParaVenta(dto);
 
        assertNotNull(resultado);
        assertEquals("BOL-001", resultado.getFolio());
        verify(boletaClient, times(1)).crearBoleta(dto);
    }
 
    @Test
    void crearBoletaParaVenta_FolioNulo_DeberiaLanzarIllegalArgumentException() {
        BoletaVentaDTO dto = new BoletaVentaDTO();
        dto.setFolio(null);
 
        assertThrows(IllegalArgumentException.class,
                () -> ventaService.crearBoletaParaVenta(dto));
        verify(boletaClient, never()).crearBoleta(any());
    }
 
    @Test
    void crearBoletaParaVenta_FolioVacio_DeberiaLanzarIllegalArgumentException() {
        BoletaVentaDTO dto = new BoletaVentaDTO();
        dto.setFolio("  ");
 
        assertThrows(IllegalArgumentException.class,
                () -> ventaService.crearBoletaParaVenta(dto));
        verify(boletaClient, never()).crearBoleta(any());
    }
 
    @Test
    void crearBoletaParaVenta_FechaFutura_DeberiaLanzarIllegalArgumentException() {
        BoletaVentaDTO dto = buildBoletaDTO("BOL-001");
        dto.setFecha(LocalDate.now().plusDays(1)); 
 
        assertThrows(IllegalArgumentException.class,
                () -> ventaService.crearBoletaParaVenta(dto));
        verify(boletaClient, never()).crearBoleta(any());
    }
 
    @Test
    void crearBoletaParaVenta_FechaHoy_DeberiaPermitirse() {
        BoletaVentaDTO dto = buildBoletaDTO("BOL-001");
        dto.setFecha(LocalDate.now());
        when(boletaClient.crearBoleta(dto)).thenReturn(dto);
 
        assertDoesNotThrow(() -> ventaService.crearBoletaParaVenta(dto));
        verify(boletaClient, times(1)).crearBoleta(dto);
    }
 
    @Test
    void crearBoletaParaVenta_SinFecha_DeberiaPermitirse() {
        BoletaVentaDTO dto = buildBoletaDTO("BOL-001");
        dto.setFecha(null); 
        when(boletaClient.crearBoleta(dto)).thenReturn(dto);
 
        assertDoesNotThrow(() -> ventaService.crearBoletaParaVenta(dto));
    }
 
    @Test
    void actualizarEstadoBoleta_Exitosa_DeberiaRetornarBoletaActualizada() {
        BoletaVentaDTO dto = buildBoletaDTO("BOL-001");
        dto.setEstado("PAGADA");
        when(boletaClient.actualizarBoleta("BOL-001", dto)).thenReturn(dto);
 
        BoletaVentaDTO resultado = ventaService.actualizarEstadoBoleta("BOL-001", dto);
 
        assertNotNull(resultado);
        assertEquals("PAGADA", resultado.getEstado());
        verify(boletaClient, times(1)).actualizarBoleta("BOL-001", dto);
    }
 
    @Test
    void actualizarEstadoBoleta_FolioNulo_DeberiaLanzarIllegalArgumentException() {
        BoletaVentaDTO dto = buildBoletaDTO("BOL-001");
 
        assertThrows(IllegalArgumentException.class,
                () -> ventaService.actualizarEstadoBoleta(null, dto));
        verify(boletaClient, never()).actualizarBoleta(anyString(), any());
    }
 
    @Test
    void actualizarEstadoBoleta_FolioVacio_DeberiaLanzarIllegalArgumentException() {
        BoletaVentaDTO dto = buildBoletaDTO("BOL-001");
 
        assertThrows(IllegalArgumentException.class,
                () -> ventaService.actualizarEstadoBoleta("  ", dto));
        verify(boletaClient, never()).actualizarBoleta(anyString(), any());
    }
 
    @Test
    void actualizarEstadoBoleta_FechaFutura_DeberiaLanzarIllegalArgumentException() {
        BoletaVentaDTO dto = buildBoletaDTO("BOL-001");
        dto.setFecha(LocalDate.now().plusDays(5));
 
        assertThrows(IllegalArgumentException.class,
                () -> ventaService.actualizarEstadoBoleta("BOL-001", dto));
        verify(boletaClient, never()).actualizarBoleta(anyString(), any());
    }
 
    @Test
    void actualizarEstadoBoleta_SinFecha_DeberiaPermitirse() {
        BoletaVentaDTO dto = buildBoletaDTO("BOL-001");
        dto.setFecha(null);
        when(boletaClient.actualizarBoleta("BOL-001", dto)).thenReturn(dto);
 
        assertDoesNotThrow(() -> ventaService.actualizarEstadoBoleta("BOL-001", dto));
    }
 
    @Test
    void eliminarBoletaDeVenta_FolioValido_DeberiaLlamarAlClient() {
        doNothing().when(boletaClient).eliminarBoleta("BOL-001");
 
        assertDoesNotThrow(() -> ventaService.eliminarBoletaDeVenta("BOL-001"));
        verify(boletaClient, times(1)).eliminarBoleta("BOL-001");
    }
 
    @Test
    void eliminarBoletaDeVenta_FolioNulo_DeberiaLanzarIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> ventaService.eliminarBoletaDeVenta(null));
        verify(boletaClient, never()).eliminarBoleta(anyString());
    }
 
    @Test
    void eliminarBoletaDeVenta_FolioVacio_DeberiaLanzarIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> ventaService.eliminarBoletaDeVenta(""));
        verify(boletaClient, never()).eliminarBoleta(anyString());
    }
}