package cl.duoc.sistemaMarket.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgregarItemRequest {
    private Long idProducto;
    private Integer cantidad;
}
