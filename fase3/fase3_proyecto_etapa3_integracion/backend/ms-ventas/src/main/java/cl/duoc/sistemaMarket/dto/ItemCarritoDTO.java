package cl.duoc.sistemaMarket.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarritoDTO {
    private Long idProducto;
    private String nombreProducto;
    private String imagenProducto;
    private String pesoProducto;
    private Integer precioUnitario;
    private Integer cantidad;
    private Integer subtotal;
}
