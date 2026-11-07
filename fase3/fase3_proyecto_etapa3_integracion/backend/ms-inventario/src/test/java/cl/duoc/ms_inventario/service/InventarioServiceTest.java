package cl.duoc.ms_inventario.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Arrays;
import cl.duoc.ms_inventario.clients.ProductoClient;
import cl.duoc.ms_inventario.dto.InventarioDTO;
import cl.duoc.ms_inventario.dto.InventarioMapper;
import cl.duoc.ms_inventario.dto.ProductoResponseDTO;
import cl.duoc.ms_inventario.exception.RecursoNoEncontradoException;
import cl.duoc.ms_inventario.model.Inventario;
import cl.duoc.ms_inventario.repository.InventarioRepository;

@ExtendWith(MockitoExtension.class)
public class InventarioServiceTest {

    @Mock
    private InventarioRepository repo;

    @Mock
    private InventarioMapper mapper;//LO AGREGAMOS PORQUE EL METODO LO USA

    @Mock
    private ProductoClient productoClient;//TAMBIEN LO AGREGAMOS PORQUE EL METODO LO USA

    @InjectMocks
    private InventarioService servicio;

    @Test
    void validarObteniendoStock_CuandoProductoNoExiste_LanzaExcepcion() {
        when(repo.findByIdProducto(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> {
            servicio.validarObteniendoStock(1L);
        });
    }

    @Test
    void validarObteniendoStock_CuandoStockEsCero_RetornaSinStock() {
        Inventario inventario = new Inventario();
        inventario.setStockActual(0);
        
        InventarioDTO dto = new InventarioDTO();
        
        when(repo.findByIdProducto(1L)).thenReturn(Optional.of(inventario));
        when(mapper.toDTO(inventario)).thenReturn(dto);

        InventarioDTO resultado = servicio.validarObteniendoStock(1L);

        assertEquals("SIN STOCK", resultado.getEstadoProducto());
    }

    @Test
    void validarObteniendoStock_CuandoStockEsMayorACero_RetornaDisponible() {
        Inventario inventario = new Inventario();
        inventario.setStockActual(10);
        
        InventarioDTO dto = new InventarioDTO();
        
        when(repo.findByIdProducto(1L)).thenReturn(Optional.of(inventario));
        when(mapper.toDTO(inventario)).thenReturn(dto);

        InventarioDTO resultado = servicio.validarObteniendoStock(1L);

        assertEquals("DISPONIBLE", resultado.getEstadoProducto());
    }

    @Test
    void validarObteniendoStock_CuandoFeignFunciona_SeteaNombre() {
        Inventario inventario = new Inventario();
        inventario.setStockActual(5);
        InventarioDTO dto = new InventarioDTO();
        
        ProductoResponseDTO feignResponse = new ProductoResponseDTO();
        feignResponse.setNombre("Coca-Cola");

        when(repo.findByIdProducto(1L)).thenReturn(Optional.of(inventario));
        when(mapper.toDTO(inventario)).thenReturn(dto);
        when(productoClient.obtenerProductoPorId(1L)).thenReturn(feignResponse);

        InventarioDTO resultado = servicio.validarObteniendoStock(1L);

        assertEquals("Coca-Cola", resultado.getNombreProducto());
    }

    @Test
    void validarObteniendoStock_CuandoFeignFalla_CapturaExcepcionYContinua() {
        // 1. GIVEN: Datos básicos que funcionan
        Long id = 1L;
        Inventario inventario = new Inventario();
        inventario.setStockActual(10);
        
        InventarioDTO dto = new InventarioDTO();
        
        when(repo.findByIdProducto(id)).thenReturn(Optional.of(inventario));
        when(mapper.toDTO(inventario)).thenReturn(dto);

        // 2. FORZAMOS EL ERROR: Simulamos que Feign lanza una excepción
        when(productoClient.obtenerProductoPorId(id))
            .thenThrow(new RuntimeException("Error de conexión"));

        // 3. WHEN: Ejecutamos el método
        // El servicio DEBE atrapar la excepción y no lanzar error hacia afuera
        InventarioDTO resultado = servicio.validarObteniendoStock(id);

        // 4. THEN: Verificamos que el flujo continuó y el objeto no es nulo
        assertNotNull(resultado);
        assertEquals("DISPONIBLE", resultado.getEstadoProducto());
        
        // Verificamos que aunque falló, no se rompió la ejecución (el nombre debería ser null o el valor por defecto)
        // Esto confirma que el código entró al CATCH y no se detuvo
        assertNull(resultado.getNombreProducto()); 
    }

    @Test
    void descontarStock_CuandoHayStockSuficiente_DeberiaDescontarYGuardar(){
        //GIVEN: HAY 19 UNIDADES EN BODEGA Y QUEREMOS SACAR 3
        Long idProducto=1L;
        int cantidadADescontar=3;

        Inventario inventario=new Inventario();
        inventario.setIdProducto(idProducto);
        inventario.setStockActual(10);

        when(repo.findByIdProducto(idProducto)).thenReturn(Optional.of(inventario));

        //WHEN EJECUTAMOS EL DESCUENTO
        servicio.descontarStock(idProducto, cantidadADescontar);

        //THEN VERIFICAMOS QUE LA MATEMATICA SE HIZO BIEN Y SE GUARDO
        assertEquals(7, inventario.getStockActual(),"El stock debio disminuir de 10 a 7");
        verify(repo, times(1)).save(inventario);// Confirmamos que se llamó al guardado en BD
    }

    @Test
    void descontarStock_CuandoStockEsInsuficiente_DeberiaLanzarRuntimeException() {
        // GIVEN: Hay 5 unidades, pero intentamos sacar 10
        Long idProducto = 1L;
        int cantidadADescontar = 10;
        
        Inventario inventario = new Inventario();
        inventario.setIdProducto(idProducto);
        inventario.setStockActual(5); 
        
        when(repo.findByIdProducto(idProducto)).thenReturn(Optional.of(inventario));
        
        // WHEN & THEN: Verificamos que salte la excepción exacta
        assertThrows(RuntimeException.class, () -> {
            servicio.descontarStock(idProducto, cantidadADescontar);
        }, "Debería lanzar RuntimeException por stock insuficiente");
        
        // Verificamos que NUNCA se intente guardar un stock negativo en la BD
        verify(repo, never()).save(any(Inventario.class));
    }

    @Test
    void descontarStock_CuandoProductoNoExiste_DeberiaLanzarExcepcion() {
        // GIVEN: El producto no existe en BD
        Long idProducto = 99L;
        when(repo.findByIdProducto(idProducto)).thenReturn(Optional.empty());
        
        // WHEN & THEN: Verificamos el orElseThrow
        assertThrows(RecursoNoEncontradoException.class, () -> {
            servicio.descontarStock(idProducto, 5);
        }, "Debería lanzar RecursoNoEncontradoException si el producto no existe");
        
        verify(repo, never()).save(any(Inventario.class));
    }

    @Test
    void descontarStock_CuandoCantidadEsCeroOMenos_LanzaException() {
    // GIVEN: Una cantidad inválida (0)
    Long idProducto = 1L;
    int cantidadInvalida = 0; 

    // WHEN & THEN:
    // Al ejecutar esto, el código entrará al IF, ejecutará el LOG y lanzará la excepción.
    // Eso es lo que JaCoCo necesita ver para poner la línea en verde.
    assertThrows(IllegalArgumentException.class, () -> {
        servicio.descontarStock(idProducto, cantidadInvalida);
    });

    // Verificación de seguridad (Escudo):
    // Como el escudo funcionó, el código debió detenerse. 
    // Verificamos que NUNCA se buscó en la BD (repo), ahorrando recursos.
    verify(repo, never()).findByIdProducto(anyLong());
    }

    @Test
    void listarProductosBajoStock_DeberiaFiltrarCorrectamenteYAsignarNombres() {
        // GIVEN: Creamos un límite de stock de 10
        int limite = 10;
        
        // Producto 1: Bajo stock (Debe pasar el filtro)
        Inventario i1 = new Inventario();
        i1.setIdProducto(1L);
        i1.setStockActual(5);
        
        // Producto 2: Alto stock (NO debe pasar el filtro)
        Inventario i2 = new Inventario();
        i2.setIdProducto(2L);
        i2.setStockActual(20);
        
        when(repo.findAll()).thenReturn(List.of(i1, i2));
        
        // Mapeamos solo el que pasará el filtro
        InventarioDTO dto1 = new InventarioDTO();
        dto1.setIdProducto(1L);
        dto1.setStockActual(5);
        when(mapper.toDTO(i1)).thenReturn(dto1);
        
        // Simulamos la respuesta exitosa de Feign
        ProductoResponseDTO productoFeign = new ProductoResponseDTO();
        productoFeign.setNombre("Fideos");
        when(productoClient.obtenerProductoPorId(1L)).thenReturn(productoFeign);

        // WHEN: Ejecutamos el método
        List<InventarioDTO> resultado = servicio.listarProductosBajoStock(limite);

        // THEN: Verificamos
        assertNotNull(resultado);
        assertEquals(1, resultado.size(), "Debería traer solo 1 producto (el de stock 5)");
        assertEquals("Fideos", resultado.get(0).getNombreProducto(), "El nombre cruzado por Feign debería asignarse");
        assertEquals(5, resultado.get(0).getStockActual());
    }

    @Test
    void listarProductosBajoStock_CuandoNoHayElementos_DeberiaRetornarListaVacia() {
        // GIVEN: La BD devuelve una lista vacía
        when(repo.findAll()).thenReturn(List.of());

        // WHEN
        List<InventarioDTO> resultado = servicio.listarProductosBajoStock(5);

        // THEN
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty(), "La lista debería estar vacía");
        
        // Verificamos que jamás intentó llamar a Feign
        verify(productoClient, never()).obtenerProductoPorId(anyLong());
    }

    @Test
    void listarProductosBajoStock_CuandoFeignFalla_DeberiaAsignarNombreNoDisponible() {
        // GIVEN: Un producto con stock bajo, pero el otro microservicio está caído
        Inventario i1 = new Inventario();
        i1.setIdProducto(1L);
        i1.setStockActual(2);
        
        when(repo.findAll()).thenReturn(List.of(i1));
        
        InventarioDTO dto1 = new InventarioDTO();
        dto1.setIdProducto(1L);
        when(mapper.toDTO(i1)).thenReturn(dto1);
        
        // ¡Magia de Mockito! Simulamos que Feign explota (arroja una excepción)
        when(productoClient.obtenerProductoPorId(1L)).thenThrow(new RuntimeException("Microservicio ms-producto inalcanzable"));

        // WHEN
        List<InventarioDTO> resultado = servicio.listarProductosBajoStock(10);

        // THEN
        assertEquals(1, resultado.size());
        assertEquals("Nombre no disponible", resultado.get(0).getNombreProducto(), "Debería asignar el nombre por defecto del bloque catch");
    }

    //
    @Test
    void listarTodo_DeberiaRetornarListaCompletaConNombres() {
        // GIVEN: 2 productos en la base de datos
        Inventario i1 = new Inventario(); i1.setIdProducto(1L); i1.setStockActual(10);
        Inventario i2 = new Inventario(); i2.setIdProducto(2L); i2.setStockActual(20);
        
        when(repo.findAll()).thenReturn(Arrays.asList(i1, i2));
        
        InventarioDTO dto1 = new InventarioDTO(); dto1.setIdProducto(1L);
        InventarioDTO dto2 = new InventarioDTO(); dto2.setIdProducto(2L);
        
        when(mapper.toDTO(i1)).thenReturn(dto1);
        when(mapper.toDTO(i2)).thenReturn(dto2);
        
        ProductoResponseDTO p1 = new ProductoResponseDTO(); p1.setNombre("Teclado");
        ProductoResponseDTO p2 = new ProductoResponseDTO(); p2.setNombre("Mouse");
        
        when(productoClient.obtenerProductoPorId(1L)).thenReturn(p1);
        when(productoClient.obtenerProductoPorId(2L)).thenReturn(p2);

        // WHEN
        List<InventarioDTO> resultado = servicio.listarTodo();

        // THEN
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Teclado", resultado.get(0).getNombreProducto());
        assertEquals("Mouse", resultado.get(1).getNombreProducto());
        
        verify(productoClient, times(1)).obtenerProductoPorId(1L);
        verify(productoClient, times(1)).obtenerProductoPorId(2L);
    }

    @Test
    void listarTodo_CuandoNoHayInventario_DeberiaRetornarListaVacia() {
        // GIVEN
        when(repo.findAll()).thenReturn(Arrays.asList());

        // WHEN
        List<InventarioDTO> resultado = servicio.listarTodo();

        // THEN
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(productoClient, never()).obtenerProductoPorId(anyLong());
    }

    @Test
    void listarTodo_DeberiaCubrirIFElseYCatch() {
        // GIVEN: Preparamos los inventarios de la BD
        Inventario inv1 = new Inventario();
        inv1.setIdProducto(1L);
        inv1.setStockActual(10);
        
        Inventario inv2 = new Inventario();
        inv2.setIdProducto(2L);
        inv2.setStockActual(0);

        when(repo.findAll()).thenReturn(Arrays.asList(inv1, inv2));

        // AQUÍ ESTABA EL DETALLE: Necesitamos setear el stock en los DTOs
        // que el mapper devuelve, para que el servicio pueda validarlos.
        InventarioDTO dto1 = new InventarioDTO();
        dto1.setIdProducto(1L);
        dto1.setStockActual(10); // <--- AGREGAR ESTO
        
        InventarioDTO dto2 = new InventarioDTO();
        dto2.setIdProducto(2L);
        dto2.setStockActual(0); // <--- AGREGAR ESTO
        
        when(mapper.toDTO(any(Inventario.class))).thenReturn(dto1, dto2);

        // Mockeamos Feign para que falle (esto dispara el CATCH)
        when(productoClient.obtenerProductoPorId(anyLong()))
            .thenThrow(new RuntimeException("Feign Falló"));

        // WHEN
        List<InventarioDTO> resultado = servicio.listarTodo();

        // THEN
        assertEquals(2, resultado.size());
        
        // Ahora sí, dto1 tendrá stock > 0 y debería ser DISPONIBLE
        assertEquals("DISPONIBLE", resultado.get(0).getEstadoProducto());
        
        // Y dto2 tendrá stock = 0 y debería ser SIN STOCK
        assertEquals("SIN STOCK", resultado.get(1).getEstadoProducto());
        
        // Verificamos el nombre por defecto del catch
        assertEquals("Nombre no disponible", resultado.get(0).getNombreProducto());
    }

    @Test
    void agregarStock_CuandoCantidadEsCeroOMenos_LanzaException() {
        // GIVEN: Una cantidad inválida
        // WHEN/THEN: Verificamos que se lance la excepción
        assertThrows(IllegalArgumentException.class, () -> {
            servicio.agregarStock(1L, 0);
        });
        
        // Verificamos que NO se llamó al repositorio, porque falló antes
        verify(repo, never()).save(any());
    }

    @Test
    void agregarStock_CuandoProductoNoExiste_LanzaException() {
        // GIVEN: El repositorio devuelve vacío
        when(repo.findByIdProducto(1L)).thenReturn(Optional.empty());

        // WHEN/THEN: Verificamos la excepción
        assertThrows(RecursoNoEncontradoException.class, () -> {
            servicio.agregarStock(1L, 10);
        });
    }

    @Test
    void agregarStock_CuandoTodoEsCorrecto_SumaCorrectamenteYGuarda() {
        // GIVEN: Un producto con stock 5
        Inventario inv = new Inventario();
        inv.setIdProducto(1L);
        inv.setStockActual(5);
        
        when(repo.findByIdProducto(1L)).thenReturn(Optional.of(inv));

        // WHEN: Agregamos 10 unidades
        servicio.agregarStock(1L, 10);

        // THEN: Verificamos que el stock sea 15 (5+10) y se guardó
        assertEquals(15, inv.getStockActual());
        verify(repo, times(1)).save(inv);
    }


}
