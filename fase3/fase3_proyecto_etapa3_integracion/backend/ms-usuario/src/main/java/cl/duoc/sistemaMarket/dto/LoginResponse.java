package cl.duoc.sistemaMarket.dto;

import cl.duoc.sistemaMarket.model.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Long idUsuario;
    private String nombreUsuario;
    private String correoUsuario;
    private TipoUsuario tipoUsuario;
}
