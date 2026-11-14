package cl.duoc.sistemaMarket.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cl.duoc.sistemaMarket.exception.GlobalExceptionHandler;
import cl.duoc.sistemaMarket.service.UbicacionService;

@ExtendWith(MockitoExtension.class)
class UbicacionControllerTest {

    @Mock
    private UbicacionService ubicacionService;

    @InjectMocks
    private UbicacionController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/regiones -> mapa de región con sus comunas")
    void regionesConComunas() throws Exception {
        Map<String, List<String>> datos = new LinkedHashMap<>();
        datos.put("Valparaíso", List.of("Valparaíso", "Quilpué"));
        datos.put("Aysén", List.of());
        when(ubicacionService.regionesConComunas()).thenReturn(datos);

        mockMvc.perform(get("/api/regiones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['Valparaíso'].length()").value(2))
                .andExpect(jsonPath("$['Valparaíso'][1]").value("Quilpué"))
                .andExpect(jsonPath("$['Aysén'].length()").value(0));
    }
}
