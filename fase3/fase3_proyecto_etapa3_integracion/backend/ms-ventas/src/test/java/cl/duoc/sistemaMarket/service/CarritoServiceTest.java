package cl.duoc.sistemaMarket.service;

import cl.duoc.sistemaMarket.client.CatalogoClient;
import cl.duoc.sistemaMarket.dto.AgregarItemRequest;
import cl.duoc.sistemaMarket.dto.CarritoDTO;
import cl.duoc.sistemaMarket.dto.ProductoRemotoDTO;
import cl.duoc.sistemaMarket.model.Carrito;
import cl.duoc.sistemaMarket.model.EstadoCarrito;
import cl.duoc.sistemaMarket.model.ItemCarrito;
import cl.duoc.sistemaMarket.repository.CarritoRepository;
import cl.duoc.sistemaMarket.repository.ItemCarritoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ItemCarritoRepository itemCarritoRepository;

    @Mock
    private CatalogoClient catalogoClient;

    @InjectMocks
    private CarritoService carritoService;

    private Carrito carritoMock;
    private ProductoRemotoDTO productoMock;

    @BeforeEach
    void setUp() {
        carritoMock = new Carrito(1L, 5L, EstadoCarrito.ACTIVO, LocalDateTime.now());
        productoMock = new ProductoRemotoDTO();
        productoMock.setId(10L);
        productoMock.setNombreProducto("Leche");
        productoMock.setPrecioProducto(1200);
        productoMock.setPesoProducto("1L");
        productoMock.setImagenProducto("img/leche.jpg");
        productoMock.setActivoProducto(true);
    }

    @Test
    void obtener_CarritoNoExiste_CreaYRetornaVacio() {
        when(carritoRepository.findByIdUsuarioAndEstadoCarrito(5L, EstadoCarrito.ACTIVO)).thenReturn(Optional.empty());
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carritoMock);
        when(itemCarritoRepository.findByIdCarrito(1L)).thenReturn(List.of());

        CarritoDTO resultado = carritoService.obtener(5L);

        assertEquals(1L, resultado.getIdCarrito());
        assertEquals(0, resultado.getTotal());
    }

    @Test
    void agregarItem_ProductoValido_AgregaYCalculaTotal() {
        when(catalogoClient.obtenerProducto(10L)).thenReturn(productoMock);
        when(carritoRepository.findByIdUsuarioAndEstadoCarrito(5L, EstadoCarrito.ACTIVO)).thenReturn(Optional.of(carritoMock));
        when(itemCarritoRepository.findByIdCarritoAndIdProducto(1L, 10L)).thenReturn(Optional.empty());
        ItemCarrito guardado = new ItemCarrito(1L, 1L, 10L, 2);
        when(itemCarritoRepository.findByIdCarrito(1L)).thenReturn(List.of(guardado));

        CarritoDTO resultado = carritoService.agregarItem(5L, new AgregarItemRequest(10L, 2));

        assertEquals(2400, resultado.getTotal());
        assertEquals(1, resultado.getItems().size());
    }

    @Test
    void agregarItem_ValidacionesFallidas_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> carritoService.agregarItem(5L, new AgregarItemRequest(null, 1)));
        assertThrows(IllegalArgumentException.class, () -> carritoService.agregarItem(5L, new AgregarItemRequest(10L, 0)));

        productoMock.setActivoProducto(false);
        when(catalogoClient.obtenerProducto(10L)).thenReturn(productoMock);
        assertThrows(IllegalArgumentException.class, () -> carritoService.agregarItem(5L, new AgregarItemRequest(10L, 1)));
    }

    @Test
    void modificarCantidad_Existe_ActualizaCantidad() {
        ItemCarrito item = new ItemCarrito(1L, 1L, 10L, 1);
        when(carritoRepository.findByIdUsuarioAndEstadoCarrito(5L, EstadoCarrito.ACTIVO)).thenReturn(Optional.of(carritoMock));
        when(itemCarritoRepository.findByIdCarritoAndIdProducto(1L, 10L)).thenReturn(Optional.of(item));
        when(itemCarritoRepository.findByIdCarrito(1L)).thenReturn(List.of(item));
        when(catalogoClient.obtenerProducto(10L)).thenReturn(productoMock);

        CarritoDTO resultado = carritoService.modificarCantidad(5L, 10L, 4);

        assertEquals(4800, resultado.getTotal());
        verify(itemCarritoRepository).save(item);
    }

    @Test
    void modificarCantidad_InvalidaONoExiste_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> carritoService.modificarCantidad(5L, 10L, 0));

        when(carritoRepository.findByIdUsuarioAndEstadoCarrito(5L, EstadoCarrito.ACTIVO)).thenReturn(Optional.of(carritoMock));
        when(itemCarritoRepository.findByIdCarritoAndIdProducto(1L, 99L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> carritoService.modificarCantidad(5L, 99L, 2));
    }

    @Test
    void quitarItem_Existe_EliminaItem() {
        ItemCarrito item = new ItemCarrito(1L, 1L, 10L, 1);
        when(carritoRepository.findByIdUsuarioAndEstadoCarrito(5L, EstadoCarrito.ACTIVO)).thenReturn(Optional.of(carritoMock));
        when(itemCarritoRepository.findByIdCarritoAndIdProducto(1L, 10L)).thenReturn(Optional.of(item));
        when(itemCarritoRepository.findByIdCarrito(1L)).thenReturn(List.of());

        CarritoDTO resultado = carritoService.quitarItem(5L, 10L);

        verify(itemCarritoRepository).delete(item);
        assertTrue(resultado.getItems().isEmpty());
    }

    @Test
    void vaciar_EliminaTodosLosItems() {
        when(carritoRepository.findByIdUsuarioAndEstadoCarrito(5L, EstadoCarrito.ACTIVO)).thenReturn(Optional.of(carritoMock));
        when(itemCarritoRepository.findByIdCarrito(1L)).thenReturn(List.of());

        CarritoDTO resultado = carritoService.vaciar(5L);

        verify(itemCarritoRepository).deleteByIdCarrito(1L);
        assertEquals(0, resultado.getTotal());
    }
}