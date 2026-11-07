package cl.duoc.msVenta.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoletaVentaDTO {
    private String folio;
    private String glosa;
    private String tipo;
    private LocalDate fecha;
    private int montoBruto;
    private int montoNeto;
    private String estado;
}