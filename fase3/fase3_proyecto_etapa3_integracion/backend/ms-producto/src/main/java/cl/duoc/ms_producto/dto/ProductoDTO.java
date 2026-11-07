package cl.duoc.ms_producto.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTO {
    @NotBlank(message = "el codigo de barra tiene que tener un valor")
    @Size(min=13,max=13,message="El codigo de barra debe tener 13 caracteres para cumplir")
    private String codigoBarra;

    @NotBlank(message = "el nombre no puede ser vacio")
    @Size(min=5,max=50,message = "el nombre no puede ser mayor a 50 caracteres")
    private String nombre;

    @NotBlank(message = "la descripcion no puede estar vacia")
    @Size(min=10,max=100,message = "la descripcion no puede ser mayor a 100 caracteres")
    private String descripcion;

    @Positive(message = "el valor debe ser mayor que cero")
    @NotNull(message = "el producto debe tener un valor obligatorio")
    private Double precioProducto;

    @NotNull(message = "el producto debe pertenecer a una categoria")
    private Long categoriaId;
    
}
