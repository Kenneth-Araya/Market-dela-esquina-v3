package cl.duoc.ms_inventario.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.duoc.ms_inventario.dto.ProductoResponseDTO;

@FeignClient(name="producto-service", url="http://localhost:8084/api/v1/productos")
public interface ProductoClient {
    @GetMapping("/{id}")
    ProductoResponseDTO obtenerProductoPorId(@PathVariable("id") Long id);
}
