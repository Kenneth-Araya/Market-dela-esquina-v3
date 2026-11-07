package cl.duoc.ms_inventario.dto;

import org.springframework.stereotype.Component;

import cl.duoc.ms_inventario.model.Inventario;

@Component
public class InventarioMapper {
    public InventarioDTO toDTO(Inventario entity){
        if (entity==null) 
            return null;
        InventarioDTO dto=new InventarioDTO();
        dto.setIdProducto(entity.getIdProducto());
        dto.setStockActual(entity.getStockActual());
        dto.setFechaVencimiento(entity.getFechaVencimiento());
        return dto;
        }

    public Inventario toEntity(InventarioDTO dto){
        if(dto==null)
            return null;
        Inventario entity=new Inventario();
        entity.setIdProducto(dto.getIdProducto());
        entity.setStockActual(dto.getStockActual());
        entity.setFechaVencimiento(dto.getFechaVencimiento());
        return entity;
    }

}
