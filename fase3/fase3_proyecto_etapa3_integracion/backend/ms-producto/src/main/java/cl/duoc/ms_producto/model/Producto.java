package cl.duoc.ms_producto.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "producto", schema = "bd_productos")
public class Producto {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank (message="El codigo de barra no puede ir vacio")
    @Size(max=13)
    @Column(nullable = false, length = 13, unique=true)
    private String codigoBarra;

    @NotBlank(message="El nombre no puede ser vacio")
    @Size(max=50)
    @Column(nullable = false, length = 50)
    private String nombre;

    @Size(max=100)
    @Column(length=100)
    private String descripcion;

    @Positive(message="el precio debe ser mayor que cero para cumplir con las reglas del negocio")
    @Column(nullable=false)
    private double precioProducto;

    @NotNull(message="la categoria del producto debe ser obligatoria")
    @Column(name="categoria_id")
    private Long categoriaId;//1. Bebidas 2.Abarrotes 3. Infusiones

}
