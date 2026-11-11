package cl.duoc.sistemaMarket.dto;

import java.time.LocalDateTime;
import java.util.List;

import cl.duoc.sistemaMarket.model.DetalleVenta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoletaDTO {
    private String numeroBoleta;
    private LocalDateTime fechaEmisionBoleta;
    private Long idVenta;
    private Long idUsuario;
    private Integer totalBoleta;
    private List<DetalleVenta> detalles;
}
