package cl.duoc.ms_inventario.dto;

import lombok.Data;

@Data
public class ProductoResponseDTO {
    private String codigoBarra;
    private String nombre;
    private String descripcion;
    private Double precioProducto;
    private Long categoriaId;
    //ya viene validados por lo que no es necesario 
    // ponerle not null ni validaciones
}
