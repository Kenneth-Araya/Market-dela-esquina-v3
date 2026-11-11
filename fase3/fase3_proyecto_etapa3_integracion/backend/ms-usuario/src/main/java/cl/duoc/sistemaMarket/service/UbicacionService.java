package cl.duoc.sistemaMarket.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cl.duoc.sistemaMarket.model.Comuna;
import cl.duoc.sistemaMarket.model.Region;
import cl.duoc.sistemaMarket.repository.ComunaRepository;
import cl.duoc.sistemaMarket.repository.RegionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UbicacionService {

    private final RegionRepository regionRepository;
    private final ComunaRepository comunaRepository;

    /** Devuelve { "Región": ["Comuna1", "Comuna2"] } para llenar los selects del registro. */
    public Map<String, List<String>> regionesConComunas() {
        Map<String, List<String>> resultado = new LinkedHashMap<>();
        for (Region region : regionRepository.findAll()) {
            List<String> comunas = comunaRepository.findByIdRegion(region.getId())
                    .stream().map(Comuna::getNombreComuna).toList();
            resultado.put(region.getNombreRegion(), comunas);
        }
        return resultado;
    }
}
