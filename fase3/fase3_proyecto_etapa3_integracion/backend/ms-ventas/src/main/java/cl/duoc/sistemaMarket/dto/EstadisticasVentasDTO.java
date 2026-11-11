package cl.duoc.sistemaMarket.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasVentasDTO {
    private long ventasTotales;
    private long ingresosTotales;
    private long ticketPromedio;
    private LocalDateTime ultimaVenta;
    private long ventasMes;
    private long ingresosMes;
}
