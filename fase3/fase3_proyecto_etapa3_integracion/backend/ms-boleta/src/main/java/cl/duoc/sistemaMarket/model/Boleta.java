package cl.duoc.sistemaMarket.model;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "boleta")
public class Boleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String folioBoleta;

    @Column(nullable = false, length = 100)
    private String glosaBoleta;

    @Column(nullable = false, length = 100)
    private String tipoBoleta;

    @Column(nullable = false, length = 100)
    private LocalDate fechaEmisionBoleta;

    @Column(nullable = false, length = 100)
    private int montoBrutoBoleta;

    @Column(nullable = false, length = 100)
    private int montoNetoBoleta;

    @Column(nullable = false, length = 100)
    private String estadoBoleta;

    // ManyToOne: muchas boletas pertenecen a una venta
    //@Column(name = "id_venta", nullable = false)
    //private Long idVenta;
}