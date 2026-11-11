package cl.duoc.sistemaMarket.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoDTO {
    private Long idCarrito;
    private Long idUsuario;
    private List<ItemCarritoDTO> items;
    private Integer subtotal;
    private Integer total;
}
