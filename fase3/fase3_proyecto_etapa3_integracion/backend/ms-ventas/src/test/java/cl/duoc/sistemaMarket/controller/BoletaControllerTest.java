package cl.duoc.sistemaMarket.controller;

import cl.duoc.sistemaMarket.dto.BoletaDTO;
import cl.duoc.sistemaMarket.service.BoletaService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BoletaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BoletaService boletaService;

    @InjectMocks
    private BoletaController boletaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(boletaController).build();
    }

    @Test
    void obtenerPorVenta_RetornaOk() throws Exception {
        BoletaDTO dto = new BoletaDTO("B-000001", LocalDateTime.now(), 1L, 2L, 5000, List.of());
        when(boletaService.obtenerPorVenta(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/boletas/venta/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroBoleta").value("B-000001"));
    }
}