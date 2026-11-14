package cl.duoc.sistemaMarket.controller;

import cl.duoc.sistemaMarket.dto.AgregarItemRequest;
import cl.duoc.sistemaMarket.dto.CantidadRequest;
import cl.duoc.sistemaMarket.dto.CarritoDTO;
import cl.duoc.sistemaMarket.service.CarritoService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CarritoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CarritoService carritoService;

    @InjectMocks
    private CarritoController carritoController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private CarritoDTO carritoMock;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(carritoController).build();
        carritoMock = new CarritoDTO(1L, 5L, List.of(), 0, 0);
    }

    @Test
    void endpointsCarrito_RetornanOk() throws Exception {
        when(carritoService.obtener(5L)).thenReturn(carritoMock);
        when(carritoService.agregarItem(eq(5L), any(AgregarItemRequest.class))).thenReturn(carritoMock);
        when(carritoService.modificarCantidad(5L, 10L, 3)).thenReturn(carritoMock);
        when(carritoService.quitarItem(5L, 10L)).thenReturn(carritoMock);
        when(carritoService.vaciar(5L)).thenReturn(carritoMock);

        mockMvc.perform(get("/api/carrito/5")).andExpect(status().isOk());

        mockMvc.perform(post("/api/carrito/5/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AgregarItemRequest(10L, 2))))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/carrito/5/items/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CantidadRequest(3))))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/carrito/5/items/10")).andExpect(status().isOk());
        mockMvc.perform(delete("/api/carrito/5")).andExpect(status().isOk());
    }
}