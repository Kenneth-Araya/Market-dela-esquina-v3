package cl.duoc.sistemaMarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasUsuarioDTO {
    private long totalUsuarios;
    private long administradores;
    private long clientes;
    private long vendedores;
}
