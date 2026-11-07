package cl.duoc.sistemaMarket.service;
 
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import cl.duoc.sistemaMarket.dto.BoletaDTO;
import cl.duoc.sistemaMarket.dto.BoletaDTOMapper;
import cl.duoc.sistemaMarket.model.Boleta;
import cl.duoc.sistemaMarket.repository.BoletaRepository;
 
@ExtendWith(MockitoExtension.class)
public class BoletaServiceTest {
 
    @Mock
    private BoletaRepository boletaRepository;
 
    @Mock
    private BoletaDTOMapper boletaDTOMapper;
 
    @InjectMocks
    private BoletaService boletaService;

    private Boleta buildBoleta(String folio) {
        Boleta b = new Boleta();
        b.setFolioBoleta(folio);
        return b;
    }
 
    private BoletaDTO buildBoletaDTO(String folio) {
        BoletaDTO dto = new BoletaDTO();
        dto.setFolio(folio);
        dto.setFecha(LocalDate.now());
        return dto;
    }
 
    @Test
    void guardarBoleta_DtoNulo_DeberiaLanzarIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> boletaService.guardarBoleta(null));
        verify(boletaRepository, never()).save(any());
    }
 
    @Test
    void guardarBoleta_FolioVacio_DeberiaLanzarIllegalArgumentException() {
        BoletaDTO dto = new BoletaDTO();
        dto.setFolio("");
 
        assertThrows(IllegalArgumentException.class,
                () -> boletaService.guardarBoleta(dto));
        verify(boletaRepository, never()).save(any());
    }
 
    @Test
    void guardarBoleta_SinFecha_DeberiaGuardarCorrectamente() {
        BoletaDTO dto = buildBoletaDTO("FOL-001");
        dto.setFecha(null);
 
        Boleta boleta = buildBoleta("FOL-001");
        when(boletaDTOMapper.toModel(dto)).thenReturn(boleta);
        when(boletaRepository.save(boleta)).thenReturn(boleta);
        when(boletaDTOMapper.toDTO(boleta)).thenReturn(dto);
 
        BoletaDTO resultado = boletaService.guardarBoleta(dto);
 
        assertNotNull(resultado);
        verify(boletaRepository, times(1)).save(any());
    }
 
    @Test
    void guardarBoleta_FechaHoy_DeberiaGuardarCorrectamente() {
        BoletaDTO dto = buildBoletaDTO("FOL-001");
        dto.setFecha(LocalDate.now());

        Boleta boleta = buildBoleta("FOL-001");
        when(boletaDTOMapper.toModel(dto)).thenReturn(boleta);
        when(boletaRepository.save(boleta)).thenReturn(boleta);
        when(boletaDTOMapper.toDTO(boleta)).thenReturn(dto);
 
        assertDoesNotThrow(() -> boletaService.guardarBoleta(dto));
        verify(boletaRepository, times(1)).save(any());
    }
 
 
    @Test
    void actualizarBoleta_FechaFutura_DeberiaLanzarIllegalArgumentException() {
        String folio = "FOL-001";
        Boleta existente = buildBoleta(folio);
        BoletaDTO dto = buildBoletaDTO(folio);
        dto.setFecha(LocalDate.now().plusDays(3));
 
        when(boletaRepository.findByFolioBoleta(folio)).thenReturn(existente);
 
        assertThrows(IllegalArgumentException.class,
                () -> boletaService.actualizarBoleta(folio, dto));
        verify(boletaRepository, never()).save(any());
    }
 
    @Test
    void actualizarBoleta_SinFecha_DeberiaActualizarCorrectamente() {
        String folio = "FOL-001";
        Boleta existente = buildBoleta(folio);
        existente.setId(1L);
 
        BoletaDTO dto = buildBoletaDTO(folio);
        dto.setFecha(null);
 
        Boleta actualizada = buildBoleta(folio);
        when(boletaRepository.findByFolioBoleta(folio)).thenReturn(existente);
        when(boletaDTOMapper.toModel(dto)).thenReturn(actualizada);
        when(boletaRepository.save(any())).thenReturn(actualizada);
 
        boolean resultado = boletaService.actualizarBoleta(folio, dto);
 
        assertTrue(resultado);
        verify(boletaRepository, times(1)).save(any());
    }
 
    @Test
    void actualizarBoleta_FechaHoy_DeberiaActualizarCorrectamente() {
        String folio = "FOL-001";
        Boleta existente = buildBoleta(folio);
        existente.setId(1L);
 
        BoletaDTO dto = buildBoletaDTO(folio);
        dto.setFecha(LocalDate.now());
 
        Boleta actualizada = buildBoleta(folio);
        when(boletaRepository.findByFolioBoleta(folio)).thenReturn(existente);
        when(boletaDTOMapper.toModel(dto)).thenReturn(actualizada);
        when(boletaRepository.save(any())).thenReturn(actualizada);
 
        boolean resultado = boletaService.actualizarBoleta(folio, dto);
 
        assertTrue(resultado);
    }
 
    @Test
    void eliminarBoleta_Exitoso_DeberiaRetornarTrue() {
        String folio = "FOL-001";
        Boleta existente = buildBoleta(folio);
        when(boletaRepository.findByFolioBoleta(folio)).thenReturn(existente);
        doNothing().when(boletaRepository).deleteByFolioBoleta(folio);
 
        boolean resultado = boletaService.eliminarBoleta(folio);
 
        assertTrue(resultado);
        verify(boletaRepository, times(1)).deleteByFolioBoleta(folio);
    }
 
    @Test
    void obtenerBoletaPorFolio_DeberiaRetornarTodosLosCampos() {
        String folio = "FOL-001";
        Boleta boleta = buildBoleta(folio);
 
        BoletaDTO dto = buildBoletaDTO(folio);
        dto.setFecha(LocalDate.of(2026, 1, 15));
 
        when(boletaRepository.findByFolioBoleta(folio)).thenReturn(boleta);
        when(boletaDTOMapper.toDTO(boleta)).thenReturn(dto);
 
        BoletaDTO resultado = boletaService.obtenerBoletaPorFolio(folio);
 
        assertNotNull(resultado);
        assertEquals(folio, resultado.getFolio());
        assertEquals(LocalDate.of(2026, 1, 15), resultado.getFecha());
        verify(boletaDTOMapper, times(1)).toDTO(boleta);
    }
 
    @Test
    void listarTodos_DeberiaLlamarMapperPorCadaBoleta() {
        Boleta b1 = buildBoleta("FOL-001");
        Boleta b2 = buildBoleta("FOL-002");
        Boleta b3 = buildBoleta("FOL-003");
 
        BoletaDTO dto1 = buildBoletaDTO("FOL-001");
        BoletaDTO dto2 = buildBoletaDTO("FOL-002");
        BoletaDTO dto3 = buildBoletaDTO("FOL-003");
 
        when(boletaRepository.findAll()).thenReturn(List.of(b1, b2, b3));
        when(boletaDTOMapper.toDTO(b1)).thenReturn(dto1);
        when(boletaDTOMapper.toDTO(b2)).thenReturn(dto2);
        when(boletaDTOMapper.toDTO(b3)).thenReturn(dto3);
 
        List<BoletaDTO> resultado = boletaService.listarTodos();
 
        assertEquals(3, resultado.size());
        verify(boletaDTOMapper, times(3)).toDTO(any(Boleta.class));
    }
}