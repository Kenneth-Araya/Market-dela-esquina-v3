package cl.duoc.sistemaMarket.controller;

import cl.duoc.sistemaMarket.model.ConfiguracionTienda;
import cl.duoc.sistemaMarket.model.Moneda;
import cl.duoc.sistemaMarket.service.ConfiguracionService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ConfiguracionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ConfiguracionService configuracionService;

    @InjectMocks
    private ConfiguracionController configuracionController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private ConfiguracionTienda configMock;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(configuracionController).build();
        configMock = new ConfiguracionTienda();
        configMock.setId(1L);
        configMock.setNombreTienda("Market De La Esquina");
        configMock.setMoneda(Moneda.CLP);
        configMock.setNotificarNuevasVentas(true);
        configMock.setModoMantenimiento(false);
    }

    @Test
    void obtener_RetornaConfiguracion() throws Exception {
        when(configuracionService.obtener()).thenReturn(configMock);

        mockMvc.perform(get("/api/configuracion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreTienda").value("Market De La Esquina"));
    }

    @Test
    void actualizar_RetornaConfiguracionActualizada() throws Exception {
        when(configuracionService.actualizar(any(ConfiguracionTienda.class))).thenReturn(configMock);

        mockMvc.perform(put("/api/configuracion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(configMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void restaurar_RetornaConfiguracionPorDefecto() throws Exception {
        when(configuracionService.restaurar()).thenReturn(configMock);

        mockMvc.perform(post("/api/configuracion/restaurar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreTienda").value("Market De La Esquina"));
    }
}