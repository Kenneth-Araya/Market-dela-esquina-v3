package cl.duoc.sistemaMarket.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    
    @NotNull(message = "El nombre es obligatorio")
    private String nombreUsuarioDto;

    @NotNull(message = "El rut es obligatorio")
    private String rutUsuarioDto;

    @Pattern(regexp = "^\\+569\\d{8}$", message = "El número debe tener el formato +569XXXXXXXX")
    @NotNull(message = "El contacto es obligatorio")
    private String contactoUsuarioDto;

    @NotNull(message = "El correo es obligatorio")
    private String correoUsuarioDto;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccionUsuarioDto;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fechaUsuarioDto;
}
