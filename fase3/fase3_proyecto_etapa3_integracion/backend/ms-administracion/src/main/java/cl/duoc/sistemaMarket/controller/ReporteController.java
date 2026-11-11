package cl.duoc.sistemaMarket.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cl.duoc.sistemaMarket.dto.DashboardDTO;
import cl.duoc.sistemaMarket.dto.ReporteGeneralDTO;
import cl.duoc.sistemaMarket.dto.UsuarioPorTipoDTO;
import cl.duoc.sistemaMarket.dto.VentaPorProductoDTO;
import cl.duoc.sistemaMarket.service.ReporteService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/general")
    public ReporteGeneralDTO general() {
        return reporteService.general();
    }

    @GetMapping("/ventas-por-producto")
    public List<VentaPorProductoDTO> ventasPorProducto() {
        return reporteService.ventasPorProducto();
    }

    @GetMapping("/usuarios-por-tipo")
    public List<UsuarioPorTipoDTO> usuariosPorTipo() {
        return reporteService.usuariosPorTipo();
    }

    @GetMapping("/dashboard")
    public DashboardDTO dashboard() {
        return reporteService.dashboard();
    }
}
