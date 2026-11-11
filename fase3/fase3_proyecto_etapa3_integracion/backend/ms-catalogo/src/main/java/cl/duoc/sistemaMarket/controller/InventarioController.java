package cl.duoc.sistemaMarket.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.sistemaMarket.dto.DescuentoRequest;
import cl.duoc.sistemaMarket.dto.MovimientoRequest;
import cl.duoc.sistemaMarket.model.Inventario;
import cl.duoc.sistemaMarket.model.MovimientoInventario;
import cl.duoc.sistemaMarket.service.InventarioService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    public List<Inventario> listar() {
        return inventarioService.listar();
    }

    @GetMapping("/{idProducto}")
    public Inventario obtener(@PathVariable Long idProducto) {
        return inventarioService.obtener(idProducto);
    }

    @GetMapping("/{idProducto}/movimientos")
    public List<MovimientoInventario> movimientos(@PathVariable Long idProducto) {
        return inventarioService.movimientos(idProducto);
    }

    @PatchMapping("/{idProducto}/stock-minimo")
    public Inventario actualizarStockMinimo(@PathVariable Long idProducto, @RequestParam Integer valor) {
        return inventarioService.actualizarStockMinimo(idProducto, valor);
    }

    @PostMapping("/movimientos")
    @ResponseStatus(HttpStatus.CREATED)
    public Inventario registrarMovimiento(@RequestBody MovimientoRequest request) {
        return inventarioService.registrarMovimiento(request);
    }

    // Uso interno: lo llama MS Ventas (el gateway no lo expone al exterior)
    @PostMapping("/descontar")
    public void descontar(@RequestBody DescuentoRequest request) {
        inventarioService.descontar(request);
    }

    // Uso interno: lo llama MS Ventas al anular una venta
    @PostMapping("/reponer")
    public void reponer(@RequestBody DescuentoRequest request) {
        inventarioService.reponer(request);
    }
}
