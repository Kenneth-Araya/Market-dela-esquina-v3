package cl.duoc.ms_inventario.dto;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventarioDTO {

    @NotNull(message = "El ID es obligatorio para vincular con el catálogo")
    private Long idProducto;

    @PositiveOrZero(message = "el stock no puede ser negativo")
    private int stockActual;

    private LocalDate fechaVencimiento;

    //PARA LA COMUNICACION CON EL MICROSERVICIO DE BOLETA
    private String estadoProducto; 

    //Aquí guardaremos el nombre que traiga Feign
    private String nombreProducto;


}
