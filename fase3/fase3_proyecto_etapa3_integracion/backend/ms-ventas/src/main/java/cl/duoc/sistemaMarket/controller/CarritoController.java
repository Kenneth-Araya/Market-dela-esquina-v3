package cl.duoc.sistemaMarket.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cl.duoc.sistemaMarket.dto.AgregarItemRequest;
import cl.duoc.sistemaMarket.dto.CantidadRequest;
import cl.duoc.sistemaMarket.dto.CarritoDTO;
import cl.duoc.sistemaMarket.service.CarritoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/carrito/{idUsuario}")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping
    public CarritoDTO obtener(@PathVariable Long idUsuario) {
        return carritoService.obtener(idUsuario);
    }

    @PostMapping("/items")
    public CarritoDTO agregarItem(@PathVariable Long idUsuario, @RequestBody AgregarItemRequest request) {
        return carritoService.agregarItem(idUsuario, request);
    }

    @PutMapping("/items/{idProducto}")
    public CarritoDTO modificarCantidad(@PathVariable Long idUsuario, @PathVariable Long idProducto,
                                        @RequestBody CantidadRequest request) {
        return carritoService.modificarCantidad(idUsuario, idProducto, request.getCantidad());
    }

    @DeleteMapping("/items/{idProducto}")
    public CarritoDTO quitarItem(@PathVariable Long idUsuario, @PathVariable Long idProducto) {
        return carritoService.quitarItem(idUsuario, idProducto);
    }

    @DeleteMapping
    public CarritoDTO vaciar(@PathVariable Long idUsuario) {
        return carritoService.vaciar(idUsuario);
    }
}
