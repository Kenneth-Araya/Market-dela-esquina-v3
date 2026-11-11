package cl.duoc.sistemaMarket.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaPorProductoDTO {
    private Long idProducto;
    private String nombreProducto;
    private Long unidadesVendidas;
    private Long ingresosGenerados;
}
