package cl.duoc.sistemaMarket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cl.duoc.sistemaMarket.model.ConfiguracionTienda;
import cl.duoc.sistemaMarket.service.ConfiguracionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/configuracion")
@RequiredArgsConstructor
public class ConfiguracionController {

    private final ConfiguracionService configuracionService;

    @GetMapping
    public ConfiguracionTienda obtener() {
        return configuracionService.obtener();
    }

    @PutMapping
    public ConfiguracionTienda actualizar(@RequestBody ConfiguracionTienda configuracion) {
        return configuracionService.actualizar(configuracion);
    }

    @PostMapping("/restaurar")
    public ConfiguracionTienda restaurar() {
        return configuracionService.restaurar();
    }
}
