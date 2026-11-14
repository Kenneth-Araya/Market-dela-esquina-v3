package cl.duoc.sistemaMarket.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.sistemaMarket.model.Comuna;
import cl.duoc.sistemaMarket.model.Region;
import cl.duoc.sistemaMarket.repository.ComunaRepository;
import cl.duoc.sistemaMarket.repository.RegionRepository;

@ExtendWith(MockitoExtension.class)
class UbicacionServiceTest {

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private ComunaRepository comunaRepository;

    @InjectMocks
    private UbicacionService ubicacionService;

    @Test
    @DisplayName("agrupa las comunas por región respetando el orden")
    void regionesConComunas() {
        Region metropolitana = new Region(1L, "Región Metropolitana");
        Region valparaiso = new Region(2L, "Valparaíso");
        when(regionRepository.findAll()).thenReturn(List.of(metropolitana, valparaiso));
        when(comunaRepository.findByIdRegion(1L)).thenReturn(List.of(
                new Comuna(10L, "Santiago", 1L),
                new Comuna(11L, "Maipú", 1L)));
        when(comunaRepository.findByIdRegion(2L)).thenReturn(List.of(
                new Comuna(20L, "Quilpué", 2L)));

        Map<String, List<String>> resultado = ubicacionService.regionesConComunas();

        assertThat(resultado.keySet()).containsExactly("Región Metropolitana", "Valparaíso");
        assertThat(resultado.get("Región Metropolitana")).containsExactly("Santiago", "Maipú");
        assertThat(resultado.get("Valparaíso")).containsExactly("Quilpué");
    }

    @Test
    @DisplayName("una región sin comunas devuelve lista vacía")
    void regionSinComunas() {
        when(regionRepository.findAll()).thenReturn(List.of(new Region(3L, "Aysén")));
        when(comunaRepository.findByIdRegion(3L)).thenReturn(List.of());

        Map<String, List<String>> resultado = ubicacionService.regionesConComunas();

        assertThat(resultado).containsOnlyKeys("Aysén");
        assertThat(resultado.get("Aysén")).isEmpty();
    }

    @Test
    @DisplayName("sin regiones devuelve un mapa vacío")
    void sinRegiones() {
        when(regionRepository.findAll()).thenReturn(List.of());

        assertThat(ubicacionService.regionesConComunas()).isEmpty();
    }
}
