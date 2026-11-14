package cl.duoc.sistemaMarket.service;

import cl.duoc.sistemaMarket.model.ConfiguracionTienda;
import cl.duoc.sistemaMarket.model.Moneda;
import cl.duoc.sistemaMarket.repository.ConfiguracionTiendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfiguracionServiceTest {

    @Mock
    private ConfiguracionTiendaRepository configuracionRepository;

    @InjectMocks
    private ConfiguracionService configuracionService;

    private ConfiguracionTienda configMock;

    @BeforeEach
    void setUp() {
        configMock = new ConfiguracionTienda();
        configMock.setId(1L);
        configMock.setNombreTienda("Market De La Esquina");
        configMock.setCorreoContacto("contacto@marketdelaesquina.cl");
        configMock.setTelefonoContacto("+56 9 1234 5678");
        configMock.setMoneda(Moneda.CLP);
        configMock.setNotificarNuevasVentas(true);
        configMock.setModoMantenimiento(false);
    }

    @Test
    void obtener_ExisteConfiguracion_RetornaConfiguracion() {
        when(configuracionRepository.findById(1L)).thenReturn(Optional.of(configMock));

        ConfiguracionTienda resultado = configuracionService.obtener();

        assertNotNull(resultado);
        assertEquals("Market De La Esquina", resultado.getNombreTienda());
        verify(configuracionRepository, times(1)).findById(1L);
    }

    @Test
    void obtener_NoExisteConfiguracion_RetornaPorDefecto() {
        when(configuracionRepository.findById(1L)).thenReturn(Optional.empty());
        when(configuracionRepository.save(any(ConfiguracionTienda.class))).thenReturn(configMock);

        ConfiguracionTienda resultado = configuracionService.obtener();

        assertNotNull(resultado);
        verify(configuracionRepository, times(1)).save(any(ConfiguracionTienda.class));
    }

    @Test
    void actualizar_DatosValidos_GuardaConfiguracion() {
        when(configuracionRepository.save(any(ConfiguracionTienda.class))).thenReturn(configMock);

        ConfiguracionTienda resultado = configuracionService.actualizar(configMock);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(true, resultado.getNotificarNuevasVentas());
        verify(configuracionRepository, times(1)).save(configMock);
    }

    @Test
    void actualizar_NombreEnBlanco_LanzaExcepcion() {
        configMock.setNombreTienda("");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            configuracionService.actualizar(configMock);
        });

        assertEquals("El nombre de la tienda es obligatorio.", ex.getMessage());
    }

    @Test
    void actualizar_NombreSuperaLimite_LanzaExcepcion() {
        configMock.setNombreTienda("A".repeat(101));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            configuracionService.actualizar(configMock);
        });

        assertTrue(ex.getMessage().contains("no puede superar los 100 caracteres"));
    }

    @Test
    void restaurar_RetornaValoresPorDefecto() {
        when(configuracionRepository.save(any(ConfiguracionTienda.class))).thenReturn(configMock);

        ConfiguracionTienda resultado = configuracionService.restaurar();

        assertNotNull(resultado);
        verify(configuracionRepository, times(1)).save(any(ConfiguracionTienda.class));
    }
}