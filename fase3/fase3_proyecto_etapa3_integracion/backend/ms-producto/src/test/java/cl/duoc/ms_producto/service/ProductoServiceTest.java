package cl.duoc.ms_producto.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.ms_producto.dto.ProductoDTO;
import cl.duoc.ms_producto.exception.RecursoNoEncontradoException;
import cl.duoc.ms_producto.model.Producto;
import cl.duoc.ms_producto.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository repo;

    @InjectMocks
    private ProductoService servicio;

    //TEST 1: LISTAR PRODUCTOS
    @Test
    void listarProductos_CuandoExistenProductos_DeberiaRetornarListaDeDtos(){
        //GIVEN: PREPARAMOS EL ESCENARIO
        Producto producto=new Producto();
        producto.setId(1L);
        producto.setNombre("Pan Hallulla");

        //SIMULAMOS QUE EL REPOSITORIO TIENE UN PRODUCTO EN LA BD
        when(repo.findAllByOrderByIdAsc()).thenReturn(List.of(producto));

        //CUANDO EJECUTEMOS EL METODO QUE QUERAMOS PROBAR
        List<ProductoDTO>resultado=servicio.listarProductos();

        //ENTONCES VERIFICAMOS LOS RESULTADOS
        assertNotNull(resultado,"la lista no deberia ir nula");
        assertEquals(1, resultado.size(),"la lista deberia tener 1 producto");
        assertEquals("Pan Hallulla", resultado.get(0).getNombre(),"El nombre no coincide");

        //VERIFICAMOS QUE SE LLAMO AL REPO 1 VEZ
        verify(repo, times(1)).findAllByOrderByIdAsc();
    }

    @Test
    void guardarProducto_CuandoNombreYaExiste_DeberiaLanzarIllegalArgumentException(){
        //GIVEN: PREPARAMOS UN DTO CON UN NOMBRE QUYE "YA EXISTA"
        ProductoDTO dto=new ProductoDTO();
        dto.setNombre("Coca-Cola");

        //SIMULAMOS QUE EL REPO NOS DICE SI, ESE NOMBRE YA EXISTE
        when(repo.existsByNombre("Coca-Cola")).thenReturn(true);

        //WHEN Y THEN: VERIFICAMOS QUE AL LLAMAR AL SERVICIO SE LANCE LA EXCEPCION
        assertThrows(IllegalArgumentException.class,()->{
            servicio.guardarProducto(dto);
        },"Debería lanzar IllegalArgumentException cuando el nombre ya existe");

        //VERIFICAMOS QUE NUNCA SE LLAMO AL GUARDADO PORQUE FALLO ANTES
        verify(repo, never()).save(any(Producto.class));
    }

    @Test
    void guardarProducto_CuandoNombreNoExista_DeberiaGuardarExitosamente(){
        //GIVEN: PREPARAMOS UN DTO Y UN PRODUCTO ENTIDAD 
        ProductoDTO dto=new ProductoDTO();
        dto.setNombre("Pepsi");
        dto.setPrecioProducto(2000.0);
    
        //SIMULAMOS QUE EL REPO DICE QUE NO EXISTE
        when(repo.existsByNombre("Pepsi")).thenReturn(false);

        //SIMULAMOS QUE AL GUARDAR EL REPO DEVOLVERA EL OBJETO GUARDADO
        Producto producto=new Producto();
        producto.setNombre("Pepsi");
        producto.setPrecioProducto(2000.0);
        when(repo.save(any(Producto.class))).thenReturn(producto);
        
        //WHEN: EJECUTAMOS EL GUARDADO
        ProductoDTO resultado=servicio.guardarProducto(dto);

        //THEN: VERIFICAMOS QUE NO SEA NULO Y QUE TENGA EL ID
        assertNotNull(resultado,"El producto guardado no deberia ser nulo");
        assertEquals(2000.0, resultado.getPrecioProducto(),"El precio debería ser 2000.0");

        verify(repo, times(1)).save(any(Producto.class));
    }

    @Test
    void buscarProducto_CuandoIdExiste_DeberiaRetornarUnProductoDTO(){
        //GIVEN: SIMULAMOS QUE AL BUSCAR EL ID 1, EL REPOSITORIO ENCUENTRA ALGO
        Producto producto=new Producto();
        producto.setId(1L);
        producto.setNombre("Pan Hallulla");
        when(repo.findById(1L)).thenReturn(Optional.of(producto));

        //WHEN: EJECUTAMOS LA BUSQUEDA
        ProductoDTO resultado=servicio.buscarProducto(1L);

        //THEN: VERIFICAMOS QUE EL RESULTADO SEA EL ESPERADO
        assertNotNull(resultado,"El producto no deberia ser nulo");
        assertEquals("Pan Hallulla", resultado.getNombre(),"El nombre deberia coincidir");

        //VERIFICAMOS QUE SE LLAMO AL REPO
        verify(repo, times(1)).findById(1L);
    }

    @Test
    void eliminarProducto_CuandoIdNoExiste_DeberiaLanzarUnRecursoNoEncontradoException(){
        //GIVEN: SIMULAR QUE EL REPO DICE QUE NO EXISTE EL ID
        when(repo.existsById(99L)).thenReturn(false);
        
        //WHEN Y THEN VERIFICAMOS QUE SE LANZA LA EXCEPTION
        assertThrows(RecursoNoEncontradoException.class, ()->{
            servicio.eliminarProducto(99L);
        },"Debería lanzar RecursoNoEncontradoException si el ID no existe");

        //VERIFICAMOS QUE NUNCA SE LLAMO AL DELETE
        verify(repo, never()).deleteById(anyLong());
    }

    @Test
    void eliminarProducto_CuandoIdExiste_DeberiaEliminarExitosamente(){
        //GIVEN: EL REPO CONFIRMA QUE EL ID EXISTE
        when(repo.existsById(1L)).thenReturn(true);

        //WHEN: EJECUTAMOS EL METODO
        servicio.eliminarProducto(1L);

        //THEN: VERIFICAMOS QUE SE LLAMO Al METODO
        verify(repo, times(1)).deleteById(1L);
    }

    @Test
    void actualizarProducto_CuandoIdNoExista_DeberiaLanzarRecursoNoEncontradoException(){
        Long idInexistente=99L;
        ProductoDTO dto=new ProductoDTO();
        dto.setNombre("nombre de prueba");

        when(repo.findById(idInexistente)).thenReturn(Optional.empty());

        //WHEN Y THEN
        assertThrows(RecursoNoEncontradoException.class, ()->{
            servicio.actualizarProducto(idInexistente, dto);
        });
    }

    @Test
    void actualizarProducto_CuandoIdExiste_DeberiaLanzarIllegalArgumentException(){
        Long id=1L;
        Producto existente=new Producto();
        existente.setId(id);
        existente.setNombre("Nombre Original");

        ProductoDTO dto=new ProductoDTO();
        dto.setNombre("Nombre Ocupado");

        //SIMULAMOS QUE EL PRODUCTO EXISTE PERO EL NOMBRE NUEVA YA ESTA USADO
        when(repo.findById(id)).thenReturn(Optional.of(existente));
        when(repo.existsByNombre("Nombre Ocupado")).thenReturn(true);

        //WHEN Y THEN
        assertThrows(IllegalArgumentException.class,()->{
            servicio.actualizarProducto(id, dto);
        },"Debería lanzar IllegalArgumentException si el nuevo nombre ya existe");

    }

    @Test
    void actualizarProducto_CuandoDatosSonValidos_DeberiaRetornarProductoActualizado(){
        //GIVEN
        Long id=1L;
        Producto producto=new Producto();
        producto.setId(id);
        producto.setNombre("Producto Original");

        ProductoDTO dto=new ProductoDTO();
        dto.setNombre("Producto Nuevo");
        dto.setPrecioProducto(500.0);
        
        when(repo.findById(id)).thenReturn(Optional.of(producto));
        when(repo.existsByNombre("Producto Nuevo")).thenReturn(false); //SIMULAMOS QUE NO ESTA OCUPADO
        when(repo.save(any(Producto.class))).thenReturn(producto);

        //WHEN:
        ProductoDTO resultado=servicio.actualizarProducto(id, dto);

        //THEN
        assertNotNull(resultado,"El resultado no deberia ser nulo");
        assertEquals("Producto Nuevo", resultado.getNombre(),"El nombre deberia actualizarse");
        verify(repo, times(1)).save(any(Producto.class));

    }


}
