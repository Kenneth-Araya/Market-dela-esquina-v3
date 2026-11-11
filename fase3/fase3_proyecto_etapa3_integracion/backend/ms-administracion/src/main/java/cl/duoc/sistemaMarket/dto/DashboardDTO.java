package cl.duoc.sistemaMarket.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private long productos;
    private long usuarios;
    private long ventasMes;
    private long ingresosMes;
}
