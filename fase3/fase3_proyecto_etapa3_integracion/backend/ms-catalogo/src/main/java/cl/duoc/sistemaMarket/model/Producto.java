package cl.duoc.sistemaMarket.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombreProducto;

    @Column(length = 1000)
    private String descripcionProducto;

    // Precio en pesos chilenos (sin decimales)
    @Column(nullable = false)
    private Integer precioProducto;

    // Ej: "500gm", "1L", "1 unidad"
    @Column(length = 50)
    private String pesoProducto;

    @Column(length = 255)
    private String imagenProducto;

    @Column
    private Double ratingProducto;

    @Column(nullable = false)
    private Boolean activoProducto = true;

    @Column
    private Long idCategoria;
}
