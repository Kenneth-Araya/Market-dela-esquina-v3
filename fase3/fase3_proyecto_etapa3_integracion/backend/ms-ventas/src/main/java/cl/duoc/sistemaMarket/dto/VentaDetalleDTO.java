package cl.duoc.sistemaMarket.dto;

import java.util.List;

import cl.duoc.sistemaMarket.model.DetalleVenta;
import cl.duoc.sistemaMarket.model.Venta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDetalleDTO {
    private Venta venta;
    private List<DetalleVenta> detalles;
}
