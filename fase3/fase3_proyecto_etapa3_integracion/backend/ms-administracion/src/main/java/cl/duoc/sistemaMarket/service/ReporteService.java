package cl.duoc.sistemaMarket.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.sistemaMarket.client.ServiciosClient;
import cl.duoc.sistemaMarket.dto.DashboardDTO;
import cl.duoc.sistemaMarket.dto.EstadisticasUsuarioDTO;
import cl.duoc.sistemaMarket.dto.EstadisticasVentasDTO;
import cl.duoc.sistemaMarket.dto.ReporteGeneralDTO;
import cl.duoc.sistemaMarket.dto.UsuarioPorTipoDTO;
import cl.duoc.sistemaMarket.dto.VentaPorProductoDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ServiciosClient serviciosClient;

    /** Tarjetas de reportesAdmin: ingresos, ventas, producto estrella, usuarios. */
    public ReporteGeneralDTO general() {
        EstadisticasVentasDTO ventas = serviciosClient.estadisticasVentas();
        EstadisticasUsuarioDTO usuarios = serviciosClient.estadisticasUsuarios();
        String estrella = serviciosClient.ventasPorProducto().stream()
                .max((a, b) -> Long.compare(a.getUnidadesVendidas(), b.getUnidadesVendidas()))
                .map(VentaPorProductoDTO::getNombreProducto)
                .orElse("-");
        return new ReporteGeneralDTO(ventas.getIngresosTotales(), ventas.getVentasTotales(),
                estrella, usuarios.getTotalUsuarios());
    }

    /** Tabla "Ventas por producto". */
    public List<VentaPorProductoDTO> ventasPorProducto() {
        return serviciosClient.ventasPorProducto();
    }

    /** Tabla "Usuarios por tipo". */
    public List<UsuarioPorTipoDTO> usuariosPorTipo() {
        EstadisticasUsuarioDTO u = serviciosClient.estadisticasUsuarios();
        return List.of(
                new UsuarioPorTipoDTO("ADMINISTRADOR", u.getAdministradores()),
                new UsuarioPorTipoDTO("CLIENTE", u.getClientes()),
                new UsuarioPorTipoDTO("VENDEDOR", u.getVendedores()));
    }

    /** Contadores del panel admin.html. */
    public DashboardDTO dashboard() {
        EstadisticasVentasDTO ventas = serviciosClient.estadisticasVentas();
        return new DashboardDTO(
                serviciosClient.totalProductos(),
                serviciosClient.estadisticasUsuarios().getTotalUsuarios(),
                ventas.getVentasMes(),
                ventas.getIngresosMes());
    }
}
