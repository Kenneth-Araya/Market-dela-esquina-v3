package cl.duoc.sistemaMarket.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteGeneralDTO {
    private long ingresosTotales;
    private long ventasTotales;
    private String productoEstrella;
    private long usuariosRegistrados;
}
