package cl.duoc.sistemaMarket.dto;

import cl.duoc.sistemaMarket.model.TipoMovimiento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoRequest {
    private Long idProducto;
    private TipoMovimiento tipoMovimiento;
    private Integer cantidad;
    private String motivo;
}
