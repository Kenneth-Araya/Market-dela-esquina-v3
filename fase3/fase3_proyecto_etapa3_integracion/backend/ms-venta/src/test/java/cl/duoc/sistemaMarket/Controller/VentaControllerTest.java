package cl.duoc.sistemaMarket.Controller;
 
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cl.duoc.msVenta.SistemaMarketApplication;
import cl.duoc.msVenta.controller.VentaController;
import cl.duoc.msVenta.dto.BoletaVentaDTO;
import cl.duoc.msVenta.dto.VentaDTO;
import cl.duoc.msVenta.service.VentaService;
 
@WebMvcTest(VentaController.class)
@ContextConfiguration(classes = SistemaMarketApplication.class)
public class VentaControllerTest {
 
    @Autowired
    private MockMvc mockMvc;
 
    @MockBean
    private VentaService ventaService;
 
    private ObjectMapper objectMapper;
 
    private VentaDTO buildVentaDTO(String descripcion) {
        VentaDTO dto = new VentaDTO();
        dto.setDescripcionVentaDto(descripcion);
        dto.setMontoPagoVentaDto(15000.0);
        dto.setCodigoTransaccionVentaDto("TXN-20260618-001");
        dto.setEstadoPagoVentaDto("PENDIENTE");
        dto.setTotalVentaDto(15000.0);
        dto.setFechaVentaDto(LocalDateTime.of(2026, 6, 18, 10, 0));
        dto.setMetodoPagoVentaDto("DEBITO");
        dto.setProductos(Collections.singletonList("PROD-001"));
        return dto;
    }
 
    private BoletaVentaDTO buildBoletaDTO(String folio) {
        BoletaVentaDTO dto = new BoletaVentaDTO();
        dto.setFolio(folio);
        dto.setEstado("PENDIENTE");
        return dto;
    }
 
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void listarVentas_DeberiaRetornar200() throws Exception {
        when(ventaService.listarTodos()).thenReturn(
                Arrays.asList(buildVentaDTO("Lacteos"), buildVentaDTO("Carbohidratos")));
 
        mockMvc.perform(get("/api/v1/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].descripcionVentaDto").value("Lacteos"))
                .andExpect(jsonPath("$[1].descripcionVentaDto").value("Carbohidratos"));
    }
 
    @Test
    void guardarVenta_DeberiaRetornar201() throws Exception {
        VentaDTO nueva = buildVentaDTO("Lacteos");
        when(ventaService.guardarVenta(any(VentaDTO.class))).thenReturn(nueva);
 
        mockMvc.perform(post("/api/v1/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descripcionVentaDto").value("Lacteos"));
    }
 
    @Test
    void eliminarVenta_DeberiaRetornar204() throws Exception {
        when(ventaService.eliminarPorId(1L)).thenReturn(true);
 
        mockMvc.perform(delete("/api/v1/ventas/{id}", 1L))
                .andExpect(status().isNoContent());
    }
 
    @Test
    void actualizarVenta_DeberiaRetornar200() throws Exception {
        VentaDTO actualizado = buildVentaDTO("Carbohidratos");
        actualizado.setMetodoPagoVentaDto("EFECTIVO");
 
        when(ventaService.actualizarVenta(eq(1L), any(VentaDTO.class))).thenReturn(actualizado);
 
        mockMvc.perform(put("/api/v1/ventas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcionVentaDto").value("Carbohidratos"));
    }
 
    @Test
    void listarVentaPorId_DeberiaRetornar200() throws Exception {
        VentaDTO venta = buildVentaDTO("Suplementos proteicos");
        venta.setCodigoTransaccionVentaDto("TXN-20260616-002");
        when(ventaService.findById(1L)).thenReturn(venta);
 
        mockMvc.perform(get("/api/v1/ventas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcionVentaDto").value("Suplementos proteicos"))
                .andExpect(jsonPath("$.codigoTransaccionVentaDto").value("TXN-20260616-002"));
    }

    @Test
    void listarVentas_ListaVacia_DeberiaRetornar200() throws Exception {
        when(ventaService.listarTodos()).thenReturn(Collections.emptyList());
 
        mockMvc.perform(get("/api/v1/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }
 
    @Test
    void listarVentas_DeberiaRetornarTodosLosCamposDelDTO() throws Exception {
        VentaDTO venta = buildVentaDTO("Bebidas");
        venta.setProductos(List.of("PROD-010", "PROD-011"));
        when(ventaService.listarTodos()).thenReturn(List.of(venta));
 
        mockMvc.perform(get("/api/v1/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].montoPagoVentaDto").value(15000.0))
                .andExpect(jsonPath("$[0].estadoPagoVentaDto").value("PENDIENTE"))
                .andExpect(jsonPath("$[0].metodoPagoVentaDto").value("DEBITO"))
                .andExpect(jsonPath("$[0].productos.size()").value(2));
    }
 
    @Test
    void listarVentaPorId_ServicioLanzaExcepcion_DeberiaRetornar500() throws Exception {
        when(ventaService.findById(999L))
                .thenThrow(new RuntimeException("Venta no encontrada"));
 
        mockMvc.perform(get("/api/v1/ventas/{id}", 999L))
                .andExpect(status().isInternalServerError());
    }
 
    @Test
    void actualizarVenta_ServicioLanzaExcepcion_DeberiaRetornar500() throws Exception {
        VentaDTO dto = buildVentaDTO("X");
        when(ventaService.actualizarVenta(eq(999L), any(VentaDTO.class)))
                .thenThrow(new RuntimeException("Venta no encontrada"));
 
        mockMvc.perform(put("/api/v1/ventas/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }
 
    @Test
    void eliminarVenta_ServicioLanzaExcepcion_DeberiaRetornar500() throws Exception {
        when(ventaService.eliminarPorId(999L))
        .thenThrow(new RuntimeException("Error al eliminar"));
 
        mockMvc.perform(delete("/api/v1/ventas/{id}", 999L))
                .andExpect(status().isInternalServerError());
    }
 
    @Test
    void guardarVenta_ServicioLanzaExcepcion_DeberiaRetornar500() throws Exception {
        VentaDTO dto = buildVentaDTO("Lacteos");
        when(ventaService.guardarVenta(any(VentaDTO.class)))
                .thenThrow(new RuntimeException("Error al persistir"));
 
        mockMvc.perform(post("/api/v1/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }
 

    @Test
    void listarBoletas_DeberiaRetornar200() throws Exception {
        BoletaVentaDTO b1 = buildBoletaDTO("BOL-001");
        BoletaVentaDTO b2 = buildBoletaDTO("BOL-002");
        when(ventaService.listarBoletas()).thenReturn(Arrays.asList(b1, b2));
 
        mockMvc.perform(get("/api/v1/ventas/boletas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].folio").value("BOL-001"))
                .andExpect(jsonPath("$[1].folio").value("BOL-002"));
    }
 
    @Test
    void listarBoletas_ListaVacia_DeberiaRetornar200() throws Exception {
        when(ventaService.listarBoletas()).thenReturn(Collections.emptyList());
 
        mockMvc.perform(get("/api/v1/ventas/boletas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }
 
    @Test
    void obtenerBoleta_DeberiaRetornar200() throws Exception {
        BoletaVentaDTO boleta = buildBoletaDTO("BOL-001");
        when(ventaService.obtenerBoletaPorFolio("BOL-001")).thenReturn(boleta);
 
        mockMvc.perform(get("/api/v1/ventas/boletas/{folio}", "BOL-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.folio").value("BOL-001"));
    }
 
    @Test
    void obtenerBoleta_ServicioLanzaExcepcion_DeberiaRetornar500() throws Exception {
        when(ventaService.obtenerBoletaPorFolio("BOL-999"))
                .thenThrow(new RuntimeException("Boleta no encontrada"));
 
        mockMvc.perform(get("/api/v1/ventas/boletas/{folio}", "BOL-999"))
                .andExpect(status().isInternalServerError());
    }
 
    @Test
    void actualizarBoleta_DeberiaRetornar200() throws Exception {
        BoletaVentaDTO dto = buildBoletaDTO("BOL-001");
        dto.setEstado("PAGADA");
        when(ventaService.actualizarEstadoBoleta(eq("BOL-001"), any(BoletaVentaDTO.class)))
                .thenReturn(dto);
 
        mockMvc.perform(put("/api/v1/ventas/boletas/{folio}", "BOL-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PAGADA"));
    }
 
    @Test
    void actualizarBoleta_ServicioLanzaExcepcion_DeberiaRetornar500() throws Exception {
        BoletaVentaDTO dto = buildBoletaDTO("BOL-999");
        when(ventaService.actualizarEstadoBoleta(eq("BOL-999"), any(BoletaVentaDTO.class)))
                .thenThrow(new RuntimeException("Boleta no encontrada"));
 
        mockMvc.perform(put("/api/v1/ventas/boletas/{folio}", "BOL-999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }
 
    @Test
    void eliminarBoleta_DeberiaRetornar204() throws Exception {
        doNothing().when(ventaService).eliminarBoletaDeVenta("BOL-001");
 
        mockMvc.perform(delete("/api/v1/ventas/boletas/{folio}", "BOL-001"))
                .andExpect(status().isNoContent());
    }
 
    @Test
    void eliminarBoleta_ServicioLanzaExcepcion_DeberiaRetornar500() throws Exception {
        doThrow(new RuntimeException("Error al eliminar boleta"))
                .when(ventaService).eliminarBoletaDeVenta("BOL-999");
 
        mockMvc.perform(delete("/api/v1/ventas/boletas/{folio}", "BOL-999"))
                .andExpect(status().isInternalServerError());
    }
}