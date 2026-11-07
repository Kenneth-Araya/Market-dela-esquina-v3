package cl.duoc.msVenta.Client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import cl.duoc.msVenta.dto.BoletaVentaDTO;

@FeignClient(name = "ms-boleta", url = "http://localhost:8081")
public interface BoletaClient {
 
    // Obtener todas las boletas
    @GetMapping("/api/v1/boletas")
    List<BoletaVentaDTO> listarBoletas();
 
    // Obtener boleta por folio
    @GetMapping("/api/v1/boletas/{folio}")
    BoletaVentaDTO obtenerBoletaPorFolio(@PathVariable("folio") String folio);
 
    // Crear una boleta nueva desde venta
    @PostMapping("/api/v1/boletas")
    BoletaVentaDTO crearBoleta(@RequestBody BoletaVentaDTO boletaDTO);
 
    // Actualizar boleta completa por folio
    @PutMapping("/api/v1/boletas/{folio}")
    BoletaVentaDTO actualizarBoleta(@PathVariable("folio") String folio,@RequestBody BoletaVentaDTO boletaDTO);
 
    // Eliminar boleta por folio
    @DeleteMapping("/api/v1/boletas/{folio}")
    void eliminarBoleta(@PathVariable("folio") String folio);
}
 