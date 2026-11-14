package cl.duoc.sistemaMarket.controller;

import cl.duoc.sistemaMarket.dto.EstadisticasVentasDTO;
import cl.duoc.sistemaMarket.dto.VentaDetalleDTO;
import cl.duoc.sistemaMarket.dto.VentaPorProductoDTO;
import cl.duoc.sistemaMarket.model.EstadoVenta;
import cl.duoc.sistemaMarket.model.Venta;
import cl.duoc.sistemaMarket.service.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VentaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VentaService ventaService;

    @InjectMocks
    private VentaController ventaController;

    private VentaDetalleDTO detalleMock;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ventaController).build();
        Venta v = new Venta(1L, 5L, LocalDateTime.now(), 3000, 3000, EstadoVenta.PAGADA);
        detalleMock = new VentaDetalleDTO(v, List.of());
    }

    @Test
    void endpointsVenta_RetornanEstadoEsperado() throws Exception {
        when(ventaService.finalizarCompra(5L)).thenReturn(detalleMock);
        when(ventaService.listar()).thenReturn(List.of(detalleMock));
        when(ventaService.estadisticas()).thenReturn(new EstadisticasVentasDTO(1L, 3000L, 3000L, LocalDateTime.now(), 1L, 3000L));
        when(ventaService.ventasPorProducto()).thenReturn(List.of(new VentaPorProductoDTO(10L, "Pan", 2L, 3000L)));
        when(ventaService.listarPorUsuario(5L)).thenReturn(List.of(detalleMock));
        when(ventaService.buscarPorId(1L)).thenReturn(detalleMock);
        when(ventaService.anular(1L)).thenReturn(detalleMock);

        mockMvc.perform(post("/api/ventas/checkout/5")).andExpect(status().isCreated());
        mockMvc.perform(get("/api/ventas")).andExpect(status().isOk());
        mockMvc.perform(get("/api/ventas/estadisticas")).andExpect(status().isOk());
        mockMvc.perform(get("/api/ventas/reportes/por-producto")).andExpect(status().isOk());
        mockMvc.perform(get("/api/ventas/usuario/5")).andExpect(status().isOk());
        mockMvc.perform(get("/api/ventas/1")).andExpect(status().isOk());
        mockMvc.perform(put("/api/ventas/1/anular")).andExpect(status().isOk());
    }
}