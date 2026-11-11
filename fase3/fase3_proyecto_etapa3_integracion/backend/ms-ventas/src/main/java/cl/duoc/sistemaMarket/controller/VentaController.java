package cl.duoc.sistemaMarket.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.sistemaMarket.dto.EstadisticasVentasDTO;
import cl.duoc.sistemaMarket.dto.VentaDetalleDTO;
import cl.duoc.sistemaMarket.dto.VentaPorProductoDTO;
import cl.duoc.sistemaMarket.service.VentaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @PostMapping("/checkout/{idUsuario}")
    @ResponseStatus(HttpStatus.CREATED)
    public VentaDetalleDTO finalizarCompra(@PathVariable Long idUsuario) {
        return ventaService.finalizarCompra(idUsuario);
    }

    @GetMapping
    public List<VentaDetalleDTO> listar() {
        return ventaService.listar();
    }

    @GetMapping("/estadisticas")
    public EstadisticasVentasDTO estadisticas() {
        return ventaService.estadisticas();
    }

    @GetMapping("/reportes/por-producto")
    public List<VentaPorProductoDTO> ventasPorProducto() {
        return ventaService.ventasPorProducto();
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<VentaDetalleDTO> listarPorUsuario(@PathVariable Long idUsuario) {
        return ventaService.listarPorUsuario(idUsuario);
    }

    @GetMapping("/{id}")
    public VentaDetalleDTO buscarPorId(@PathVariable Long id) {
        return ventaService.buscarPorId(id);
    }

    @PutMapping("/{id}/anular")
    public VentaDetalleDTO anular(@PathVariable Long id) {
        return ventaService.anular(id);
    }
}
