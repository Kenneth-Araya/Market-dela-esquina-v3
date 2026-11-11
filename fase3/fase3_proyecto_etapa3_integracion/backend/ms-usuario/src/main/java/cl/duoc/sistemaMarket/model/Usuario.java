package cl.duoc.sistemaMarket.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Column(nullable = false, unique = true, length = 9)
    private String runUsuario;

    @Column(nullable = false, length = 50)
    private String nombreUsuario;

    @Column(nullable = false, length = 100)
    private String apellidosUsuario;

    @Column(nullable = false, unique = true, length = 100)
    private String correoUsuario;

    @Column
    private LocalDate fechaNacimientoUsuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoUsuario tipoUsuario;

    @Column(nullable = false, length = 100)
    private String regionUsuario;

    @Column(nullable = false, length = 100)
    private String comunaUsuario;

    @Column(nullable = false, length = 300)
    private String direccionUsuario;

    // Se recibe en el JSON pero nunca se devuelve
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 100)
    private String contrasenaUsuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoUsuario estadoUsuario;

    @Column(nullable = false)
    private LocalDateTime fechaRegistroUsuario;
}
