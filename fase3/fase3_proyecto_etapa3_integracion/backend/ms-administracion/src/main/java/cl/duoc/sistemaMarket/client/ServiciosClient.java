package cl.duoc.sistemaMarket.client;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import cl.duoc.sistemaMarket.dto.EstadisticasUsuarioDTO;
import cl.duoc.sistemaMarket.dto.EstadisticasVentasDTO;
import cl.duoc.sistemaMarket.dto.VentaPorProductoDTO;
import lombok.RequiredArgsConstructor;

/** Consulta a los otros microservicios. Los reportes no guardan copias de sus datos. */
@Component
@RequiredArgsConstructor
public class ServiciosClient {

    private final RestTemplate restTemplate;

    @Value("${usuario.url}")
    private String usuarioUrl;

    @Value("${catalogo.url}")
    private String catalogoUrl;

    @Value("${ventas.url}")
    private String ventasUrl;

    public EstadisticasUsuarioDTO estadisticasUsuarios() {
        return restTemplate.getForObject(usuarioUrl + "/api/usuarios/estadisticas", EstadisticasUsuarioDTO.class);
    }

    public long totalProductos() {
        Long total = restTemplate.getForObject(catalogoUrl + "/api/productos/total", Long.class);
        return total == null ? 0 : total;
    }

    public EstadisticasVentasDTO estadisticasVentas() {
        return restTemplate.getForObject(ventasUrl + "/api/ventas/estadisticas", EstadisticasVentasDTO.class);
    }

    public List<VentaPorProductoDTO> ventasPorProducto() {
        VentaPorProductoDTO[] datos = restTemplate.getForObject(
                ventasUrl + "/api/ventas/reportes/por-producto", VentaPorProductoDTO[].class);
        return datos == null ? List.of() : Arrays.asList(datos);
    }
}
