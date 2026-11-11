package cl.duoc.sistemaMarket.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

/** Vista del producto tal como lo entrega MS Catalogo. */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductoRemotoDTO {
    private Long id;
    private String nombreProducto;
    private Integer precioProducto;
    private String pesoProducto;
    private String imagenProducto;
    private Boolean activoProducto;
}
