package cl.duoc.ms_producto.dto;

import cl.duoc.ms_producto.model.Producto;

public class ProductoDTOMapper {

    public static Producto toEntity(ProductoDTO dto){
        if(dto==null){
            return null;
        }
        
        Producto producto=new Producto();
        producto.setCodigoBarra(dto.getCodigoBarra());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecioProducto(dto.getPrecioProducto());
        producto.setCategoriaId(dto.getCategoriaId());
        return producto;

    }

    public static ProductoDTO toDto(Producto prod){
        if(prod==null){
            return null;
        }

        ProductoDTO dto=new ProductoDTO();
        dto.setCodigoBarra(prod.getCodigoBarra());
        dto.setNombre(prod.getNombre());
        dto.setDescripcion(prod.getDescripcion());
        dto.setPrecioProducto(prod.getPrecioProducto());
        dto.setCategoriaId(prod.getCategoriaId());

        return dto;
    }


}
