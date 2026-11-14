package cl.duoc.sistemaMarket.controller;

import cl.duoc.sistemaMarket.model.Categoria;
import cl.duoc.sistemaMarket.service.CategoriaService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private CategoriaController categoriaController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Categoria categoriaMock;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoriaController).build();
        categoriaMock = new Categoria(1L, "Despensa", "Abarrotes");
    }

    @Test
    void listar_RetornaOk() throws Exception {
        when(categoriaService.listar()).thenReturn(List.of(categoriaMock));

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCategoria").value("Despensa"));
    }

    @Test
    void buscarPorId_RetornaOk() throws Exception {
        when(categoriaService.buscarPorId(1L)).thenReturn(categoriaMock);

        mockMvc.perform(get("/api/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void crear_RetornaCreated() throws Exception {
        when(categoriaService.crear(any(Categoria.class))).thenReturn(categoriaMock);

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaMock)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreCategoria").value("Despensa"));
    }

    @Test
    void actualizar_RetornaOk() throws Exception {
        when(categoriaService.actualizar(eq(1L), any(Categoria.class))).thenReturn(categoriaMock);

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoriaMock)))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_RetornaNoContent() throws Exception {
        doNothing().when(categoriaService).eliminar(1L);

        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().isNoContent());
    }
}