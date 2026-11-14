package cl.duoc.sistemaMarket.service;

import cl.duoc.sistemaMarket.model.EstadoContacto;
import cl.duoc.sistemaMarket.model.MensajeContacto;
import cl.duoc.sistemaMarket.repository.MensajeContactoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactoServiceTest {

    @Mock
    private MensajeContactoRepository mensajeRepository;

    @InjectMocks
    private ContactoService contactoService;

    private MensajeContacto mensajeMock;

    @BeforeEach
    void setUp() {
        mensajeMock = new MensajeContacto();
        mensajeMock.setId(1L);
        mensajeMock.setNombreContacto("Juan Perez");
        mensajeMock.setCorreoContacto("juan@correo.cl");
        mensajeMock.setContenidoContacto("Consulta sobre productos");
        mensajeMock.setFechaEnvioContacto(LocalDateTime.now());
        mensajeMock.setEstadoContacto(EstadoContacto.NUEVO);
    }

    @Test
    void enviar_MensajeValido_GuardaMensaje() {
        when(mensajeRepository.save(any(MensajeContacto.class))).thenReturn(mensajeMock);

        MensajeContacto resultado = contactoService.enviar(mensajeMock);

        assertNotNull(resultado);
        assertEquals(EstadoContacto.NUEVO, resultado.getEstadoContacto());
        verify(mensajeRepository, times(1)).save(any(MensajeContacto.class));
    }

    @Test
    void enviar_SinNombre_LanzaExcepcion() {
        mensajeMock.setNombreContacto("");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            contactoService.enviar(mensajeMock);
        });

        assertEquals("Por favor, ingresa tu nombre completo.", ex.getMessage());
    }

    @Test
    void enviar_CorreoInvalido_LanzaExcepcion() {
        mensajeMock.setCorreoContacto("correosinroba.com");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            contactoService.enviar(mensajeMock);
        });

        assertEquals("Por favor, ingrese un correo electr\u00f3nico v\u00e1lido.", ex.getMessage());
    }

    @Test
    void listar_RetornaListaOrdenada() {
        when(mensajeRepository.findAllByOrderByFechaEnvioContactoDesc()).thenReturn(List.of(mensajeMock));

        List<MensajeContacto> resultado = contactoService.listar();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorId_Encontrado_RetornaMensaje() {
        when(mensajeRepository.findById(1L)).thenReturn(Optional.of(mensajeMock));

        MensajeContacto resultado = contactoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Juan Perez", resultado.getNombreContacto());
    }

    @Test
    void buscarPorId_NoEncontrado_LanzaExcepcion() {
        when(mensajeRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            contactoService.buscarPorId(2L);
        });
    }

    @Test
    void cambiarEstado_Valido_ActualizaEstado() {
        when(mensajeRepository.findById(1L)).thenReturn(Optional.of(mensajeMock));
        when(mensajeRepository.save(any(MensajeContacto.class))).thenReturn(mensajeMock);

        MensajeContacto resultado = contactoService.cambiarEstado(1L, EstadoContacto.LEIDO);

        assertEquals(EstadoContacto.LEIDO, resultado.getEstadoContacto());
        verify(mensajeRepository, times(1)).save(mensajeMock);
    }

    @Test
    void cambiarEstado_EstadoNulo_LanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            contactoService.cambiarEstado(1L, null);
        });

        assertEquals("El estado es obligatorio.", ex.getMessage());
    }

    @Test
    void eliminar_Encontrado_EliminaMensaje() {
        when(mensajeRepository.findById(1L)).thenReturn(Optional.of(mensajeMock));

        contactoService.eliminar(1L);

        verify(mensajeRepository, times(1)).delete(mensajeMock);
    }
}