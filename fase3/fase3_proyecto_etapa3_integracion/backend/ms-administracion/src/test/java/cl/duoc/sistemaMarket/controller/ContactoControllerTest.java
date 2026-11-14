package cl.duoc.sistemaMarket.controller;

import cl.duoc.sistemaMarket.dto.EstadoRequest;
import cl.duoc.sistemaMarket.model.EstadoContacto;
import cl.duoc.sistemaMarket.model.MensajeContacto;
import cl.duoc.sistemaMarket.service.ContactoService;
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
class ContactoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ContactoService contactoService;

    @InjectMocks
    private ContactoController contactoController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MensajeContacto mensajeMock;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(contactoController).build();
        mensajeMock = new MensajeContacto();
        mensajeMock.setId(1L);
        mensajeMock.setNombreContacto("Juan Perez");
        mensajeMock.setCorreoContacto("juan@correo.cl");
        mensajeMock.setContenidoContacto("Consulta");
        mensajeMock.setEstadoContacto(EstadoContacto.NUEVO);
    }

    @Test
    void enviar_RetornaCreated() throws Exception {
        when(contactoService.enviar(any(MensajeContacto.class))).thenReturn(mensajeMock);

        mockMvc.perform(post("/api/contactos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mensajeMock)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreContacto").value("Juan Perez"));
    }

    @Test
    void listar_RetornaLista() throws Exception {
        when(contactoService.listar()).thenReturn(List.of(mensajeMock));

        mockMvc.perform(get("/api/contactos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void buscarPorId_RetornaMensaje() throws Exception {
        when(contactoService.buscarPorId(1L)).thenReturn(mensajeMock);

        mockMvc.perform(get("/api/contactos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correoContacto").value("juan@correo.cl"));
    }

    @Test
    void cambiarEstado_RetornaActualizado() throws Exception {
        mensajeMock.setEstadoContacto(EstadoContacto.LEIDO);
        when(contactoService.cambiarEstado(eq(1L), eq(EstadoContacto.LEIDO))).thenReturn(mensajeMock);

        EstadoRequest request = new EstadoRequest(EstadoContacto.LEIDO);

        mockMvc.perform(put("/api/contactos/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoContacto").value("LEIDO"));
    }

    @Test
    void eliminar_RetornaNoContent() throws Exception {
        doNothing().when(contactoService).eliminar(1L);

        mockMvc.perform(delete("/api/contactos/1"))
                .andExpect(status().isNoContent());
    }
}