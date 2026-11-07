package cl.duoc.sistemaMarket.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import cl.duoc.sistemaMarket.dto.BoletaDTO;
import cl.duoc.sistemaMarket.dto.BoletaDTOMapper;
import cl.duoc.sistemaMarket.exeptions.RecursoNoEncontradoException;
import cl.duoc.sistemaMarket.model.Boleta;
import cl.duoc.sistemaMarket.repository.BoletaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BoletaService {

    private final BoletaRepository boletaRepository;
    private final BoletaDTOMapper boletaDTOMapper;

    // Listar todas las boletas
    public List<BoletaDTO> listarTodos() {
        log.info("Iniciando consulta para listar todas las boletas");

        List<Boleta> boletas = boletaRepository.findAll();
        List<BoletaDTO> boletaDTOs = new ArrayList<>();

        if (!boletas.isEmpty()) {
            for (Boleta boleta : boletas) {
                boletaDTOs.add(boletaDTOMapper.toDTO(boleta));
            }
            log.info("Se encontraron {} boleta(s)", boletas.size());
        } else {
            log.warn("No se encontraron boletas en el sistema");
        }

        return boletaDTOs;
    }

    // Obtener boleta por folio
    public BoletaDTO obtenerBoletaPorFolio(String folio) {
        log.info("Buscando boleta con folio: {}", folio);

        Boleta boleta = boletaRepository.findByFolioBoleta(folio);

        if (boleta == null || !boleta.getFolioBoleta().equals(folio)) {
            log.error("Boleta no encontrada con folio: {}", folio);
            throw new RecursoNoEncontradoException("Numero de folio incorrecto");
        }

        log.info("Boleta con folio {} encontrada exitosamente", folio);
        return boletaDTOMapper.toDTO(boleta);
    }

    // Guardar boleta
    public BoletaDTO  guardarBoleta(BoletaDTO boletaDTO) {
        log.info("Iniciando proceso de guardado de boleta");

        // Regla de negocio: folio no nulo ni vacío
        if (boletaDTO == null || boletaDTO.getFolio() == null || boletaDTO.getFolio().isEmpty()) {
            log.error("Validación fallida: la boleta o su número de folio son nulos o vacíos");
            throw new IllegalArgumentException("La boleta o su número de folio no pueden ser nulos o vacíos");
        }

        // Regla de negocio: fecha no futura
        if (boletaDTO.getFecha() != null && boletaDTO.getFecha().isAfter(LocalDate.now())) {
            log.error("Validación fallida: la fecha {} es posterior a la fecha actual para el folio {}",
                    boletaDTO.getFecha(), boletaDTO.getFolio());
            throw new IllegalArgumentException("La fecha de emision no puede ser una fecha futura");
        }

        Boleta boleta = boletaDTOMapper.toModel(boletaDTO);
        Boleta guardado = boletaRepository.save(boleta);
        log.info("Boleta con folio {} guardada exitosamente", boletaDTO.getFolio());
        return boletaDTOMapper.toDTO(guardado);
    }

    // Actualizar boleta
    public boolean actualizarBoleta(String folio, BoletaDTO boletaDTO) {
        log.info("Iniciando proceso de actualización para la boleta con folio: {}", folio);

        Boleta boletaExistente = boletaRepository.findByFolioBoleta(folio);

        if (boletaExistente == null || !boletaExistente.getFolioBoleta().equals(folio)) {
            log.error("No se encontró boleta con folio {} para actualizar", folio);
            throw new RecursoNoEncontradoException("Número de folio incorrecto.");
        }

        // Regla de negocio: fecha no futura
        if (boletaDTO.getFecha() != null && boletaDTO.getFecha().isAfter(LocalDate.now())) {
            log.error("Validación fallida: la fecha {} es futura para el folio {}", boletaDTO.getFecha(), folio);
            throw new IllegalArgumentException("La fecha de emision no puede ser una fecha futura");
        }

        Boleta boletaActualizada = boletaDTOMapper.toModel(boletaDTO);
        boletaActualizada.setId(boletaExistente.getId());
        boolean actualizado = boletaRepository.save(boletaActualizada) != null;

        if (actualizado) {
            log.info("Boleta con folio {} actualizada exitosamente", folio);
        } else {
            log.warn("No se pudo actualizar la boleta con folio {}", folio);
        }

        return actualizado;
    }

    // Eliminar boleta
    public boolean eliminarBoleta(String folio) {
        log.info("Iniciando proceso de eliminación para la boleta con folio: {}", folio);

        Boleta boletaExistente = boletaRepository.findByFolioBoleta(folio);

        if (boletaExistente == null || !boletaExistente.getFolioBoleta().equals(folio)) {
            log.error("No se encontró boleta con folio {} para eliminar", folio);
            throw new RecursoNoEncontradoException("Número de folio incorrecto.");
        }

        boletaRepository.deleteByFolioBoleta(folio);
        log.info("Boleta con folio {} eliminada exitosamente", folio);
        return true;
    }
}
