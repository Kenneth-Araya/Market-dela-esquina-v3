package cl.duoc.sistemaMarket.controller;

import cl.duoc.sistemaMarket.dto.DashboardDTO;
import cl.duoc.sistemaMarket.dto.ReporteGeneralDTO;
import cl.duoc.sistemaMarket.dto.UsuarioPorTipoDTO;
import cl.duoc.sistemaMarket.dto.VentaPorProductoDTO;
import cl.duoc.sistemaMarket.service.ReporteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReporteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReporteService reporteService;

    @InjectMocks
    private ReporteController reporteController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reporteController).build();
    }

    @Test
    void general_RetornaReporteGeneral() throws Exception {
        when(reporteService.general()).thenReturn(new ReporteGeneralDTO(500000L, 100L, "Arroz", 50L));

        mockMvc.perform(get("/api/reportes/general"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productoEstrella").value("Arroz"));
    }

    @Test
    void ventasPorProducto_RetornaLista() throws Exception {
        when(reporteService.ventasPorProducto()).thenReturn(List.of(new VentaPorProductoDTO(1L, "Arroz", 50L, 50000L)));

        mockMvc.perform(get("/api/reportes/ventas-por-producto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreProducto").value("Arroz"));
    }

    @Test
    void usuariosPorTipo_RetornaLista() throws Exception {
        when(reporteService.usuariosPorTipo()).thenReturn(List.of(new UsuarioPorTipoDTO("CLIENTE", 45L)));

        mockMvc.perform(get("/api/reportes/usuarios-por-tipo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoUsuario").value("CLIENTE"));
    }

    @Test
    void dashboard_RetornaMetricas() throws Exception {
        when(reporteService.dashboard()).thenReturn(new DashboardDTO(150L, 50L, 20L, 100000L));

        mockMvc.perform(get("/api/reportes/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productos").value(150));
    }
}