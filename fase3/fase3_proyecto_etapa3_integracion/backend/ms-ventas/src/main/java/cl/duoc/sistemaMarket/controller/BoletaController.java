package cl.duoc.sistemaMarket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.sistemaMarket.dto.BoletaDTO;
import cl.duoc.sistemaMarket.service.BoletaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/boletas")
@RequiredArgsConstructor
public class BoletaController {

    private final BoletaService boletaService;

    @GetMapping("/venta/{idVenta}")
    public BoletaDTO obtenerPorVenta(@PathVariable Long idVenta) {
        return boletaService.obtenerPorVenta(idVenta);
    }
}
