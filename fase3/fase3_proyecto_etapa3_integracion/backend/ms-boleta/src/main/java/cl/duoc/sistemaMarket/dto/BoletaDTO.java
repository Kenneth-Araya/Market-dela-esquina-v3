package cl.duoc.sistemaMarket.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoletaDTO {
    
    @NotNull(message = "El número de folio es obligatorio")
    private String folio;

    @NotBlank(message = "La glosa es obligatoria")
    private String glosa;

    private String tipo;

    @NotNull(message = "La fecha de emisión es obligatoria")
    @PastOrPresent(message = "La fecha de emisión no puede ser una fecha futura")
    private LocalDate fecha;

    private int montoBruto;

    @Min(value = 1, message = "El monto neto no puede ser negativo ni 0")
    private int montoNeto;

    @NotNull(message = "El estado de emisión es obligatoria")
    private String estado;
}
