package cl.duoc.ms_producto.service;
import java.util.List;
import org.springframework.stereotype.Service;
import cl.duoc.ms_producto.dto.ProductoDTO;
import cl.duoc.ms_producto.dto.ProductoDTOMapper;
import cl.duoc.ms_producto.exception.RecursoNoEncontradoException;
import cl.duoc.ms_producto.model.Producto;
import cl.duoc.ms_producto.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoService {

    private final ProductoRepository repo;


    //Listar producto
    public List<ProductoDTO> listarProductos(){
        log.info("Listando productos...");
        List<Producto> productos = repo.findAllByOrderByIdAsc();
        return productos.stream()
        .map(ProductoDTOMapper::toDto)
        .toList();
    }

    //Guardar producto
    public ProductoDTO guardarProducto(ProductoDTO dto){
        log.info("Iniciando proceso para guardar producto...");
        if (repo.existsByNombre(dto.getNombre())) {
            log.error("Error el nombre del producto {} ya se encuentra registrado ",dto.getNombre());
            throw new IllegalArgumentException("No se puede registrar el producto debido a que ya existe un registro con el nombre: " + dto.getNombre());
        }

        Producto producto =ProductoDTOMapper.toEntity(dto);
        Producto guardado=repo.save(producto);

        log.info("Producto {} guardado exitosamente con id: {} ",dto.getNombre(),guardado.getId());
        return ProductoDTOMapper.toDto(guardado);
    }

    //Eliminar producto
    public void eliminarProducto(Long id){
        log.info("Solicitando eliminacion del producto con id: {}",id);
        if (!repo.existsById(id)) {
            log.warn("No se encontro el producto con id: {}",id);
            throw new RecursoNoEncontradoException("No existe el ID: " + id);
        }

        repo.deleteById(id);
        log.info("Producto con el id: {} fue eliminado exitosamente de la base de datos ",id);
    }

    //Buscar producto por id 
    public ProductoDTO buscarProducto(Long id){
        log.info("Buscando producto...");

        Producto producto=repo.findById(id)

            .orElseThrow(()->{
                log.warn("No se encuentra el producto con id: {} ",id);
                return new RecursoNoEncontradoException("No existe el producto con ID: " + id);
            });
        log.info("Producto {} encontrado con exito",producto.getNombre());
        return ProductoDTOMapper.toDto(producto);
    }

    //Actualizar producto
    public ProductoDTO actualizarProducto(Long id,ProductoDTO dto){
        log.info("Actualizando producto...");
        Producto producto=repo.findById(id)
            .orElseThrow(()->{
                log.error("Error al actualizar producto numero {} , no existe",id);
                return new RecursoNoEncontradoException("No se puede actualizar debido a que no existe el producto con ID: " + id);
            });
        if (!producto.getNombre().equalsIgnoreCase(dto.getNombre()) && repo.existsByNombre(dto.getNombre())) {
            log.error("Error al actualizar: El nombre '{}' ya se encuentra registrado", dto.getNombre());
            throw new IllegalArgumentException("No se puede actualizar. El nombre '" + dto.getNombre() + "' ya está en uso por otro producto.");
            
        }

            producto.setCodigoBarra(dto.getCodigoBarra());
            producto.setNombre(dto.getNombre());
            producto.setDescripcion(dto.getDescripcion());
            producto.setPrecioProducto(dto.getPrecioProducto());
            producto.setCategoriaId(dto.getCategoriaId());
            
            Producto actualizado=repo.save(producto);
            log.info("Producto con id {} fue actualizado exitosamente ",actualizado.getId());
            return ProductoDTOMapper.toDto(actualizado);
        }


}
