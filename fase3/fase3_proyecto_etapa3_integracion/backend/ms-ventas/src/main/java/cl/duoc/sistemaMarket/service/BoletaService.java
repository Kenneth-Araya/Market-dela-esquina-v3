package cl.duoc.sistemaMarket.service;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import cl.duoc.sistemaMarket.dto.BoletaDTO;
import cl.duoc.sistemaMarket.model.Boleta;
import cl.duoc.sistemaMarket.model.Venta;
import cl.duoc.sistemaMarket.repository.BoletaRepository;
import cl.duoc.sistemaMarket.repository.DetalleVentaRepository;
import cl.duoc.sistemaMarket.repository.VentaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoletaService {

    private final BoletaRepository boletaRepository;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    public BoletaDTO obtenerPorVenta(Long idVenta) {
        Boleta boleta = boletaRepository.findByIdVenta(idVenta)
                .orElseThrow(() -> new NoSuchElementException("No existe boleta para la venta " + idVenta));
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new NoSuchElementException("Venta no encontrada: " + idVenta));
        return new BoletaDTO(boleta.getNumeroBoleta(), boleta.getFechaEmisionBoleta(), idVenta,
                venta.getIdUsuario(), boleta.getTotalBoleta(), detalleVentaRepository.findByIdVenta(idVenta));
    }
}
