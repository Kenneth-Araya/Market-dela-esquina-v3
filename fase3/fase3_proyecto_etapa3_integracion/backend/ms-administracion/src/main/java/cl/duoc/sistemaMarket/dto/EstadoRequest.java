package cl.duoc.sistemaMarket.dto;

import cl.duoc.sistemaMarket.model.EstadoContacto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoRequest {
    private EstadoContacto estadoContacto;
}
