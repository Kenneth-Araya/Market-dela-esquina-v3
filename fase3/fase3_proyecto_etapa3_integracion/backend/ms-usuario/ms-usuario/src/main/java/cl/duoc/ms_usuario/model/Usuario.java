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

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombreUsuario;

    @Column(nullable = false, unique = true, length = 100)
    private String rutUsuario;

    @Column(nullable = false, length = 12)
    private String contactoUsuario;

    @Column(nullable = false, length = 100)
    private String correoUsuario;

    @Column(nullable = false, length = 200)
    private String direccionUsuario;

    @Column(nullable = false)
    private LocalDate fechaUsuario;
}