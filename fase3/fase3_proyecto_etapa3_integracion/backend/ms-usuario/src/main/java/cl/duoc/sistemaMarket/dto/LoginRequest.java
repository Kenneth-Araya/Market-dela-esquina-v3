package cl.duoc.sistemaMarket.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String correoUsuario;
    private String contrasenaUsuario;
}
