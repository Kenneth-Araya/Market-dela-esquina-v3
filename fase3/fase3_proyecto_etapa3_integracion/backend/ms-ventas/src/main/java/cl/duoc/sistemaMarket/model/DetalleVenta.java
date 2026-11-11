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

// Guarda nombre y precio AL MOMENTO de la compra: un cambio de precio posterior no altera ventas pasadas
@Entity
@Table(name = "detalle_venta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idVenta;

    @Column(nullable = false)
    private Long idProducto;

    @Column(nullable = false, length = 150)
    private String nombreProducto;

    @Column(nullable = false)
    private Integer precioUnitarioDetalle;

    @Column(nullable = false)
    private Integer cantidadDetalle;

    @Column(nullable = false)
    private Integer subtotalDetalle;
}
