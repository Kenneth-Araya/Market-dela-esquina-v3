package cl.duoc.msVenta.model;

import java.time.LocalDateTime;
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
@Table(name = "venta")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVenta;

    @Column(nullable = false)
    private LocalDateTime fechaVenta;

    @Column(nullable = false)
    private Double totalVenta;

    @Column(nullable = false)
    private String descripcionVenta;

    @Column(nullable = false)
    private Double montoVenta;

    @Column(nullable = false)
    private String metodoVenta;      

    @Column(nullable = false)
    private String estadoVenta;       

    @Column(nullable = false, unique = true)
    private String codigoTransaccionVenta;      
    
    //ManyToOne: muchas ventas pertenecen a un usuario
    //@Column(name = "id_usuario", nullable = false)
    //private Long idUsuario;
}