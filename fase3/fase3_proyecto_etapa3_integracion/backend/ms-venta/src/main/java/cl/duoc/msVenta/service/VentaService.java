package cl.duoc.msVenta.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cl.duoc.msVenta.Client.BoletaClient;
import cl.duoc.msVenta.dto.BoletaVentaDTO;
import cl.duoc.msVenta.dto.VentaDTO;
import cl.duoc.msVenta.dto.VentaDTOMapper;
import cl.duoc.msVenta.exeptions.RecursoNoEncontradoException;
import cl.duoc.msVenta.model.Venta;
import cl.duoc.msVenta.repository.VentaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VentaService {


    private final VentaRepository ventaRepository; 
    private final BoletaClient boletaClient;

    // Listar ventas
    public List<VentaDTO> listarTodos() {
        log.info("Iniciando consulta para listar todas las ventas");

        List<Venta> ventas = ventaRepository.findAll();
        List<VentaDTO> ventaDTOs = new ArrayList<>();

        if (!ventas.isEmpty()) {
            for (Venta venta : ventas) {
                ventaDTOs.add(VentaDTOMapper.toDto(venta));
            }
            log.info("Se encontraron {} venta(s)", ventas.size());
        } else {
            log.warn("No se encontraron ventas en el sistema");
        }
        return ventaDTOs;
    }

    // Guardar ventas
    public VentaDTO guardarVenta(VentaDTO ventaDTO) {

        log.info("Iniciando proceso de guardado de venta");

        // regla de negocio: debe tener producto
        if (ventaDTO.getProductos() == null || ventaDTO.getProductos().isEmpty()) {
            log.error("Validación fallida: la venta con codigo transaccion venta {} no tiene productos asosciados",
                    ventaDTO.getCodigoTransaccionVentaDto());
            throw new IllegalArgumentException("No se puede generar venta sin producto");
        }

        // regla de negocio: no puede ser nula
        if (ventaDTO == null || ventaDTO.getCodigoTransaccionVentaDto() == null
                || ventaDTO.getCodigoTransaccionVentaDto().isEmpty()) {
            log.error("Validación fallida: la venta o codigo de transaccion son nulos o vacíos");
            throw new IllegalArgumentException("La venta o su codigo de transaccion no pueden ser nulos o vacios");
        }

        Venta venta = VentaDTOMapper.toEntity(ventaDTO);
        Venta guardado = ventaRepository.save(venta);

        if (guardado != null) {
            log.info("Venta con codigo de transaccion {} guarda exitosamente", ventaDTO.getCodigoTransaccionVentaDto());
        } else {
            log.warn("No se pudo guardar la venta con codigo de transaccion {}",
                    ventaDTO.getCodigoTransaccionVentaDto());
        }
        return VentaDTOMapper.toDto(guardado);
    }

    // Eliminar ventas
    public boolean eliminarPorId(Long idVenta) {
        log.info("Iniciando proceso de eliminación para la venta con id: {}", idVenta);

        Venta ventaExistente = ventaRepository.findByIdVenta(idVenta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada con id: " + idVenta));

        if (ventaExistente == null || !ventaExistente.getIdVenta().equals(idVenta)) {
            log.error("No se encontró venta con id {} para eliminar", idVenta);
            throw new RecursoNoEncontradoException("id incorrecto.");
        }

        ventaRepository.deleteByIdVenta(idVenta);
        log.info("Venta con id {} eliminada exitosamente", idVenta);
        return true;

    }

    public VentaDTO actualizarVenta(Long idVenta, VentaDTO ventaDTO) {
        log.info("Iniciando proceso de actualización para venta con id: {}", idVenta);

        // regla de negocio: codigo de transaccion no puede ser nulo
        if (ventaDTO == null || ventaDTO.getCodigoTransaccionVentaDto() == null
                || ventaDTO.getCodigoTransaccionVentaDto().isEmpty()) {
            log.error("Validación fallida: la venta o codigo de transaccion son nulos o vacíos");
            throw new IllegalArgumentException("La venta o su codigo de transaccion no pueden ser nulos o vacios");
        }

        // regla de negocio
        if (ventaRepository.existsByDescripcionVenta(ventaDTO.getDescripcionVentaDto())) {
        throw new IllegalArgumentException("Ya existe una venta con esa descripcion");
}

        // regla de negocio: debe tener productos
        if (ventaDTO.getProductos() == null || ventaDTO.getProductos().isEmpty()) {
            log.error("Validación fallida: la venta con codigo {} no tiene productos asociados",
                    ventaDTO.getCodigoTransaccionVentaDto());
            throw new IllegalArgumentException("No se puede actualizar una venta sin productos");
        }

        Venta ventaExistente = ventaRepository.findByIdVenta(idVenta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada con id: " + idVenta));

        Venta ventaActualizada = VentaDTOMapper.toEntity(ventaDTO);
        ventaActualizada.setIdVenta(ventaExistente.getIdVenta());
        Venta guardado = ventaRepository.save(ventaActualizada);

        log.info("Venta con codigo de transaccion {} actualizada exitosamente",
                ventaDTO.getCodigoTransaccionVentaDto());

        return VentaDTOMapper.toDto(guardado);
    }



    //====================================feign====================================
    
    // buscar venta por id
    public VentaDTO findById(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));
        return VentaDTOMapper.toDto(venta);
    }

    public List<BoletaVentaDTO> listarBoletas() {
        log.info("Consultando todas las boletas en ms-boleta");
        List<BoletaVentaDTO> boletas = boletaClient.listarBoletas();
        log.info("Se obtuvieron {} boleta(s) desde ms-boleta", boletas.size());
        return boletas;
    }

    //obtener boleta por folio
    public BoletaVentaDTO obtenerBoletaPorFolio(String folio) {
        log.info("Consultando boleta con folio {} en ms-boleta", folio);

        if (folio == null || folio.isBlank()) {
            throw new IllegalArgumentException("El folio no puede ser nulo o vacío");
        }

        BoletaVentaDTO boleta = boletaClient.obtenerBoletaPorFolio(folio);
        log.info("Boleta con folio {} obtenida exitosamente desde ms-boleta", folio);
        return boleta;
    }

    //crear boleta para venta
    public BoletaVentaDTO crearBoletaParaVenta(BoletaVentaDTO boletaDTO) {
        log.info("Creando boleta en ms-boleta para venta con folio {}", boletaDTO.getFolio());

        if (boletaDTO.getFolio() == null || boletaDTO.getFolio().isBlank()) {
            throw new IllegalArgumentException("El folio de la boleta no puede ser nulo o vacío");
        }

        // Regla de negocio: fecha no futura
        if (boletaDTO.getFecha() != null && boletaDTO.getFecha().isAfter(LocalDate.now())) {
            log.error("Validación fallida: la fecha {} es futura para el folio {}", boletaDTO.getFecha(), boletaDTO.getFolio());
            throw new IllegalArgumentException("La fecha de emision no puede ser una fecha futura");
        }

        BoletaVentaDTO creada = boletaClient.crearBoleta(boletaDTO);
        log.info("Boleta con folio {} creada exitosamente en ms-boleta", creada.getFolio());
        return creada;
    }

    //actualizar datos (pagada)
    public BoletaVentaDTO actualizarEstadoBoleta(String folio, BoletaVentaDTO boletaDTO) {
        log.info("Actualizando boleta con folio {} en ms-boleta", folio);

        if (folio == null || folio.isBlank()) {
            throw new IllegalArgumentException("El folio no puede ser nulo o vacío");
        }

        // Regla de negocio: fecha no futura
        if (boletaDTO.getFecha() != null && boletaDTO.getFecha().isAfter(LocalDate.now())) {
            log.error("Validación fallida: la fecha {} es futura para el folio {}", boletaDTO.getFecha(), folio);
            throw new IllegalArgumentException("La fecha de emision no puede ser una fecha futura");
        }

        BoletaVentaDTO actualizada = boletaClient.actualizarBoleta(folio, boletaDTO);
        log.info("Boleta con folio {} actualizada a estado {} en ms-boleta", folio, actualizada.getEstado());
        return actualizada;
    }

    //eliminar boleta
    public void eliminarBoletaDeVenta(String folio) {
        log.info("Eliminando boleta con folio {} en ms-boleta", folio);

        if (folio == null || folio.isBlank()) {
            throw new IllegalArgumentException("El folio no puede ser nulo o vacío");
        }

        boletaClient.eliminarBoleta(folio);
        log.info("Boleta con folio {} eliminada exitosamente en ms-boleta", folio);
    }
}
