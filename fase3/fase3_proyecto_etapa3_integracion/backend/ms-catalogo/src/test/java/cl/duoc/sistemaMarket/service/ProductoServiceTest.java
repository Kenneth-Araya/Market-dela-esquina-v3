package cl.duoc.sistemaMarket.service;

import cl.duoc.sistemaMarket.model.Inventario;
import cl.duoc.sistemaMarket.model.Producto;
import cl.duoc.sistemaMarket.repository.CategoriaRepository;
import cl.duoc.sistemaMarket.repository.InventarioRepository;
import cl.duoc.sistemaMarket.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto productoMock;

    @BeforeEach
    void setUp() {
        productoMock = new Producto(1L, "Arroz", "Arroz grado 1", 1500, "1kg", "img/arroz.jpg", 4.5, true, 1L);
    }

    @Test
    void listar_IncluirInactivosTrue_RetornaTodos() {
        when(productoRepository.findAll()).thenReturn(List.of(productoMock));

        List<Producto> resultado = productoService.listar(true, null);

        assertEquals(1, resultado.size());
        verify(productoRepository).findAll();
    }

    @Test
    void listar_ConIdCategoria_RetornaActivosPorCategoria() {
        when(productoRepository.findByIdCategoriaAndActivoProductoTrue(1L)).thenReturn(List.of(productoMock));

        List<Producto> resultado = productoService.listar(false, 1L);

        assertEquals(1, resultado.size());
        verify(productoRepository).findByIdCategoriaAndActivoProductoTrue(1L);
    }

    @Test
    void listar_SoloActivos_RetornaActivos() {
        when(productoRepository.findByActivoProductoTrue()).thenReturn(List.of(productoMock));

        List<Producto> resultado = productoService.listar(false, null);

        assertEquals(1, resultado.size());
        verify(productoRepository).findByActivoProductoTrue();
    }

    @Test
    void buscarPorId_Existe_RetornaProducto() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoMock));

        Producto resultado = productoService.buscarPorId(1L);

        assertEquals("Arroz", resultado.getNombreProducto());
    }

    @Test
    void buscarPorId_NoExiste_LanzaExcepcion() {
        when(productoRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> productoService.buscarPorId(2L));
    }

    @Test
    void totalActivos_RetornaCantidad() {
        when(productoRepository.countByActivoProductoTrue()).thenReturn(12L);

        assertEquals(12L, productoService.totalActivos());
    }

    @Test
    void crear_Valido_GuardaProductoEInventario() {
        productoMock.setActivoProducto(null);
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoMock);

        Producto resultado = productoService.crear(productoMock);

        assertNotNull(resultado);
        assertTrue(resultado.getActivoProducto());
        verify(inventarioRepository, times(1)).save(any(Inventario.class));
    }

    @Test
    void crear_ValidacionesFallidas_LanzaExcepcion() {
        productoMock.setNombreProducto("");
        assertThrows(IllegalArgumentException.class, () -> productoService.crear(productoMock));

        productoMock.setNombreProducto("a".repeat(151));
        assertThrows(IllegalArgumentException.class, () -> productoService.crear(productoMock));

        productoMock.setNombreProducto("Arroz");
        productoMock.setDescripcionProducto("a".repeat(1001));
        assertThrows(IllegalArgumentException.class, () -> productoService.crear(productoMock));

        productoMock.setDescripcionProducto("Desc");
        productoMock.setPrecioProducto(0);
        assertThrows(IllegalArgumentException.class, () -> productoService.crear(productoMock));

        productoMock.setPrecioProducto(1000);
        productoMock.setRatingProducto(6.0);
        assertThrows(IllegalArgumentException.class, () -> productoService.crear(productoMock));

        productoMock.setRatingProducto(4.5);
        when(categoriaRepository.existsById(1L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> productoService.crear(productoMock));
    }

    @Test
    void actualizar_Existe_ActualizaCampos() {
        Producto datos = new Producto(null, "Arroz Integral", "Desc", 1800, "1kg", "img.jpg", 4.8, false, 2L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoMock));
        when(categoriaRepository.existsById(anyLong())).thenReturn(true);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoMock);

        Producto resultado = productoService.actualizar(1L, datos);

        assertEquals("Arroz Integral", resultado.getNombreProducto());
        assertFalse(resultado.getActivoProducto());
    }

    @Test
    void desactivar_Existe_CambiaActivoAFalse() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoMock));

        productoService.desactivar(1L);

        assertFalse(productoMock.getActivoProducto());
        verify(productoRepository).save(productoMock);
    }
}