package cl.duoc.ms_inventario.model;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
@Entity
@Table(name = "inventario", schema = "bd_inventario")
public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInventario;

    @Column(name = "id_producto", nullable = false)
    private Long idProducto;

    @Column(nullable = false )
    @PositiveOrZero // el stock no puede ser negativo 
    private int stockActual;

    @Column(nullable = false)
    @PositiveOrZero // el stock minimo tampoco puede ser negativo 
    private int stockMinimo;

    @Column(nullable = true) // no todos los productos tienen una fecha de vencimiento 
    private LocalDate fechaVencimiento;


}
