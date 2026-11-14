package cl.duoc.sistemaMarket.controller;

import cl.duoc.sistemaMarket.model.Producto;
import cl.duoc.sistemaMarket.service.ProductoService;
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
class ProductoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Producto productoMock;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productoController).build();
        productoMock = new Producto(1L, "Arroz", "Grado 1", 1500, "1kg", "img.jpg", 4.5, true, 1L);
    }

    @Test
    void listar_RetornaOk() throws Exception {
        when(productoService.listar(false, null)).thenReturn(List.of(productoMock));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreProducto").value("Arroz"));
    }

    @Test
    void total_RetornaTotal() throws Exception {
        when(productoService.totalActivos()).thenReturn(20L);

        mockMvc.perform(get("/api/productos/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("20"));
    }

    @Test
    void buscarPorId_RetornaOk() throws Exception {
        when(productoService.buscarPorId(1L)).thenReturn(productoMock);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precioProducto").value(1500));
    }

    @Test
    void crear_RetornaCreated() throws Exception {
        when(productoService.crear(any(Producto.class))).thenReturn(productoMock);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoMock)))
                .andExpect(status().isCreated());
    }

    @Test
    void actualizar_RetornaOk() throws Exception {
        when(productoService.actualizar(eq(1L), any(Producto.class))).thenReturn(productoMock);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoMock)))
                .andExpect(status().isOk());
    }

    @Test
    void desactivar_RetornaNoContent() throws Exception {
        doNothing().when(productoService).desactivar(1L);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }
}