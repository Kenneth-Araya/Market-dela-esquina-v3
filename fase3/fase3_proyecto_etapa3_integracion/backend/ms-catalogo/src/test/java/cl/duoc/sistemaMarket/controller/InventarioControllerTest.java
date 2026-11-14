package cl.duoc.sistemaMarket.controller;

import cl.duoc.sistemaMarket.dto.DescuentoRequest;
import cl.duoc.sistemaMarket.dto.ItemStockRequest;
import cl.duoc.sistemaMarket.dto.MovimientoRequest;
import cl.duoc.sistemaMarket.model.Inventario;
import cl.duoc.sistemaMarket.model.MovimientoInventario;
import cl.duoc.sistemaMarket.model.TipoMovimiento;
import cl.duoc.sistemaMarket.service.InventarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InventarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InventarioService inventarioService;

    @InjectMocks
    private InventarioController inventarioController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Inventario inventarioMock;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(inventarioController).build();
        inventarioMock = new Inventario(1L, 10L, 50, 5);
    }

    @Test
    void listar_RetornaOk() throws Exception {
        when(inventarioService.listar()).thenReturn(List.of(inventarioMock));

        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stockActual").value(50));
    }

    @Test
    void obtener_RetornaOk() throws Exception {
        when(inventarioService.obtener(10L)).thenReturn(inventarioMock);

        mockMvc.perform(get("/api/inventario/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto").value(10));
    }

    @Test
    void movimientos_RetornaOk() throws Exception {
        MovimientoInventario mov = new MovimientoInventario(1L, 10L, TipoMovimiento.ENTRADA, 5, "compra", null, LocalDateTime.now());
        when(inventarioService.movimientos(10L)).thenReturn(List.of(mov));

        mockMvc.perform(get("/api/inventario/10/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cantidadMovimiento").value(5));
    }

    @Test
    void actualizarStockMinimo_RetornaOk() throws Exception {
        inventarioMock.setStockMinimo(10);
        when(inventarioService.actualizarStockMinimo(10L, 10)).thenReturn(inventarioMock);

        mockMvc.perform(patch("/api/inventario/10/stock-minimo").param("valor", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockMinimo").value(10));
    }

    @Test
    void registrarMovimiento_RetornaCreated() throws Exception {
        MovimientoRequest req = new MovimientoRequest(10L, TipoMovimiento.ENTRADA, 5, "compra");
        when(inventarioService.registrarMovimiento(any(MovimientoRequest.class))).thenReturn(inventarioMock);

        mockMvc.perform(post("/api/inventario/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void descontar_RetornaOk() throws Exception {
        DescuentoRequest req = new DescuentoRequest(1L, List.of(new ItemStockRequest(10L, 2)));
        doNothing().when(inventarioService).descontar(any(DescuentoRequest.class));

        mockMvc.perform(post("/api/inventario/descontar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void reponer_RetornaOk() throws Exception {
        DescuentoRequest req = new DescuentoRequest(1L, List.of(new ItemStockRequest(10L, 2)));
        doNothing().when(inventarioService).reponer(any(DescuentoRequest.class));

        mockMvc.perform(post("/api/inventario/reponer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
}