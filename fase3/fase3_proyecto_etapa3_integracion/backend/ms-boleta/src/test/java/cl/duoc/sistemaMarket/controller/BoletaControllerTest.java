package cl.duoc.sistemaMarket.controller;
 
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cl.duoc.sistemaMarket.dto.BoletaDTO;
import cl.duoc.sistemaMarket.exeptions.RecursoNoEncontradoException;
import cl.duoc.sistemaMarket.service.BoletaService;
 
@WebMvcTest(BoletaController.class)
public class BoletaControllerTest {
 
    @Autowired
    private MockMvc mockMvc;
 
    @MockBean
    private BoletaService boletaService;
 
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
 
    private BoletaDTO buildBoletaDTO(String folio) {
        BoletaDTO dto = new BoletaDTO();
        dto.setFolio(folio);
        dto.setEstado("EMITIDA");
        dto.setFecha(LocalDate.of(2024, 1, 15));
        dto.setGlosa("Compra de artículos de oficina");
        dto.setMontoBruto(11900);
        dto.setMontoNeto(10000);
        dto.setTipo("BOLETA");
        return dto;
    }
 

    @Test
    void listarBoletas_DeberiaRetornar200() throws Exception {
        BoletaDTO b1 = buildBoletaDTO("FOL-Y3P84N");
        BoletaDTO b2 = buildBoletaDTO("FOL-Z7R15Q");
        when(boletaService.listarTodos()).thenReturn(Arrays.asList(b1, b2));
 
        mockMvc.perform(get("/api/v1/boletas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].folio").value("FOL-Y3P84N"))
                .andExpect(jsonPath("$[1].folio").value("FOL-Z7R15Q"));
    }
 
    @Test
    void listarBoletas_ListaVacia_DeberiaRetornar200() throws Exception {
        when(boletaService.listarTodos()).thenReturn(Collections.emptyList());
 
        mockMvc.perform(get("/api/v1/boletas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }
 
    @Test
    void listarBoletas_ErrorServicio_DeberiaRetornar500() throws Exception {
        when(boletaService.listarTodos())
                .thenThrow(new RuntimeException("Error de base de datos"));
 
        mockMvc.perform(get("/api/v1/boletas"))
                .andExpect(status().isInternalServerError());
    }
 

    @Test
    void guardarBoleta_DeberiaRetornar200() throws Exception {
        BoletaDTO nueva = buildBoletaDTO("FOL-A00001");
        when(boletaService.guardarBoleta(any(BoletaDTO.class))).thenReturn(nueva);
 
        mockMvc.perform(post("/api/v1/boletas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.folio").value("FOL-A00001"));
    }
 
    @Test
    void guardarBoleta_ErrorServicio_DeberiaRetornar500() throws Exception {
        BoletaDTO dto = buildBoletaDTO("FOL-A00001");
        when(boletaService.guardarBoleta(any(BoletaDTO.class)))
                .thenThrow(new RuntimeException("Error al guardar"));
 
        mockMvc.perform(post("/api/v1/boletas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }
 
    @Test
    void guardarBoleta_DeberiaRetornarTodosLosCampos() throws Exception {
        BoletaDTO dto = buildBoletaDTO("FOL-A00001");
        when(boletaService.guardarBoleta(any(BoletaDTO.class))).thenReturn(dto);
 
        mockMvc.perform(post("/api/v1/boletas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.folio").value("FOL-A00001"))
                .andExpect(jsonPath("$.estado").value("EMITIDA"))
                .andExpect(jsonPath("$.glosa").value("Compra de artículos de oficina"))
                .andExpect(jsonPath("$.montoBruto").value(11900))
                .andExpect(jsonPath("$.montoNeto").value(10000))
                .andExpect(jsonPath("$.tipo").value("BOLETA"));
    }

    @Test
    void guardarBoleta_FolioVacio_DeberiaRetornar500() throws Exception {
        BoletaDTO dto = new BoletaDTO();
        dto.setFolio("");
 
        when(boletaService.guardarBoleta(any(BoletaDTO.class)))
                .thenThrow(new IllegalArgumentException("Folio no puede ser vacío"));
 
        mockMvc.perform(post("/api/v1/boletas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }
 
    @Test
    void guardarBoleta_FechaFutura_DeberiaRetornar500() throws Exception {
        BoletaDTO dto = buildBoletaDTO("FOL-A00001");
        dto.setFecha(LocalDate.now().plusDays(5));
 
        when(boletaService.guardarBoleta(any(BoletaDTO.class)))
                .thenThrow(new IllegalArgumentException("La fecha no puede ser futura"));
 
        mockMvc.perform(post("/api/v1/boletas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }
 

    @Test
    void actualizarBoleta_DeberiaRetornar200() throws Exception {
        String folio = "FOL-000000";
        BoletaDTO dto = buildBoletaDTO("FOL-A00002");
        dto.setEstado("PAGADA");
 
        when(boletaService.actualizarBoleta(eq(folio), any(BoletaDTO.class))).thenReturn(true);
 
        mockMvc.perform(put("/api/v1/boletas/{folio}", folio)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
 
    @Test
    void actualizarBoleta_ErrorServicio_DeberiaRetornar500() throws Exception {
        String folio = "FOL-999";
        BoletaDTO dto = buildBoletaDTO(folio);
 
        when(boletaService.actualizarBoleta(eq(folio), any(BoletaDTO.class)))
                .thenThrow(new RuntimeException("Error al actualizar"));
 
        mockMvc.perform(put("/api/v1/boletas/{folio}", folio)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }
 
    @Test
    void actualizarBoleta_FolioNoExiste_DeberiaRetornar404() throws Exception {
        String folio = "NO-EXISTE";
        BoletaDTO dto = buildBoletaDTO(folio);
 
        when(boletaService.actualizarBoleta(eq(folio), any(BoletaDTO.class)))
                .thenThrow(new RecursoNoEncontradoException("Folio no encontrado"));
 
        mockMvc.perform(put("/api/v1/boletas/{folio}", folio)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }
 
    @Test
    void actualizarBoleta_FechaFutura_DeberiaRetornar500() throws Exception {
        String folio = "FOL-000000";
        BoletaDTO dto = buildBoletaDTO(folio);
        dto.setFecha(LocalDate.now().plusDays(3));
 
        when(boletaService.actualizarBoleta(eq(folio), any(BoletaDTO.class)))
                .thenThrow(new IllegalArgumentException("La fecha no puede ser futura"));
 
        mockMvc.perform(put("/api/v1/boletas/{folio}", folio)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }
 
    @Test
    void eliminarBoleta_DeberiaRetornar204() throws Exception {
        when(boletaService.eliminarBoleta("FOL-000000")).thenReturn(true);
 
        mockMvc.perform(delete("/api/v1/boletas/{folio}", "FOL-000000"))
                .andExpect(status().isNoContent());
    }
 
    @Test
    void eliminarBoleta_ErrorServicio_DeberiaRetornar500() throws Exception {
        when(boletaService.eliminarBoleta("FOL-999"))
                .thenThrow(new RuntimeException("Error al eliminar"));
 
        mockMvc.perform(delete("/api/v1/boletas/{folio}", "FOL-999"))
                .andExpect(status().isInternalServerError());
    }
 
    @Test
    void eliminarBoleta_FolioNoExiste_DeberiaRetornar404() throws Exception {
        when(boletaService.eliminarBoleta("NO-EXISTE"))
                .thenThrow(new RecursoNoEncontradoException("Folio no encontrado"));
 
        mockMvc.perform(delete("/api/v1/boletas/{folio}", "NO-EXISTE"))
                .andExpect(status().isNotFound());
    }
 

    @Test
    void obtenerBoletaPorFolio_DeberiaRetornar200() throws Exception {
        String folio = "FOL-000000";
        BoletaDTO boleta = buildBoletaDTO(folio);
 
        when(boletaService.obtenerBoletaPorFolio(folio)).thenReturn(boleta);
 
        mockMvc.perform(get("/api/v1/boletas/{folio}", folio))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.folio").value(folio))
                .andExpect(jsonPath("$.estado").value("EMITIDA"));
    }
 
    @Test
    void obtenerBoletaPorFolio_ErrorServicio_DeberiaRetornar500() throws Exception {
        String folio = "FOL-999";
        when(boletaService.obtenerBoletaPorFolio(folio))
                .thenThrow(new RuntimeException("Error inesperado"));
 
        mockMvc.perform(get("/api/v1/boletas/{folio}", folio))
                .andExpect(status().isInternalServerError());
    }
 
    @Test
    void obtenerBoletaPorFolio_DeberiaRetornarTodosLosCampos() throws Exception {
        String folio = "FOL-B00015";
        BoletaDTO boleta = buildBoletaDTO(folio);
        boleta.setEstado("PAGADA");
        boleta.setMontoBruto(35700);
        boleta.setMontoNeto(30000);
        boleta.setTipo("BOLETA ELECTRONICA");
 
        when(boletaService.obtenerBoletaPorFolio(folio)).thenReturn(boleta);
 
        mockMvc.perform(get("/api/v1/boletas/{folio}", folio))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.folio").value(folio))
                .andExpect(jsonPath("$.estado").value("PAGADA"))
                .andExpect(jsonPath("$.montoBruto").value(35700))
                .andExpect(jsonPath("$.montoNeto").value(30000))
                .andExpect(jsonPath("$.tipo").value("BOLETA ELECTRONICA"));
    }
 
    @Test
    void obtenerBoletaPorFolio_FolioNoExiste_DeberiaRetornar404() throws Exception {
        String folio = "NO-EXISTE";
        when(boletaService.obtenerBoletaPorFolio(folio))
                .thenThrow(new RecursoNoEncontradoException("Folio no encontrado"));
 
        mockMvc.perform(get("/api/v1/boletas/{folio}", folio))
                .andExpect(status().isNotFound());
    }
}