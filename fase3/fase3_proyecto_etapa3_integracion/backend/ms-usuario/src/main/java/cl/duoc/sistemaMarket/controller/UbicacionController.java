package cl.duoc.sistemaMarket.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.sistemaMarket.service.UbicacionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/regiones")
@RequiredArgsConstructor
public class UbicacionController {

    private final UbicacionService ubicacionService;

    @GetMapping
    public Map<String, List<String>> regionesConComunas() {
        return ubicacionService.regionesConComunas();
    }
}
