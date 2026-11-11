package cl.duoc.sistemaMarket.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Tabla de un solo registro (id = 1)
@Entity
@Table(name = "configuracion_tienda")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionTienda {

    @Id
    private Long id = 1L;

    @Column(nullable = false, length = 100)
    private String nombreTienda;

    @Column(length = 100)
    private String correoContacto;

    @Column(length = 20)
    private String telefonoContacto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Moneda moneda;

    @Column(length = 300)
    private String direccionTienda;

    @Column(length = 100)
    private String horarioAtencion;

    @Column(nullable = false)
    private Boolean notificarNuevasVentas;

    // Si es true, el frontend oculta la tienda a los clientes
    @Column(nullable = false)
    private Boolean modoMantenimiento;
}
