package cl.duoc.sistemaMarket.service;

import cl.duoc.sistemaMarket.dto.DescuentoRequest;
import cl.duoc.sistemaMarket.dto.ItemStockRequest;
import cl.duoc.sistemaMarket.dto.MovimientoRequest;
import cl.duoc.sistemaMarket.model.Inventario;
import cl.duoc.sistemaMarket.model.MovimientoInventario;
import cl.duoc.sistemaMarket.model.Producto;
import cl.duoc.sistemaMarket.model.TipoMovimiento;
import cl.duoc.sistemaMarket.repository.InventarioRepository;
import cl.duoc.sistemaMarket.repository.MovimientoInventarioRepository;
import cl.duoc.sistemaMarket.repository.ProductoRepository;
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
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private Inventario inventarioMock;

    @BeforeEach
    void setUp() {
        inventarioMock = new Inventario(1L, 10L, 50, 5);
    }

    @Test
    void listar_RetornaLista() {
        when(inventarioRepository.findAll()).thenReturn(List.of(inventarioMock));

        assertEquals(1, inventarioService.listar().size());
    }

    @Test
    void obtener_Existe_RetornaInventario() {
        when(inventarioRepository.findByIdProducto(10L)).thenReturn(Optional.of(inventarioMock));

        Inventario resultado = inventarioService.obtener(10L);

        assertEquals(50, resultado.getStockActual());
    }

    @Test
    void obtener_NoExiste_LanzaExcepcion() {
        when(inventarioRepository.findByIdProducto(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> inventarioService.obtener(99L));
    }

    @Test
    void movimientos_RetornaHistorial() {
        MovimientoInventario mov = new MovimientoInventario(1L, 10L, TipoMovimiento.ENTRADA, 10, "compra", null, LocalDateTime.now());
        when(movimientoRepository.findByIdProductoOrderByFechaMovimientoDesc(10L)).thenReturn(List.of(mov));

        assertEquals(1, inventarioService.movimientos(10L).size());
    }

    @Test
    void actualizarStockMinimo_Valido_Actualiza() {
        when(inventarioRepository.findByIdProducto(10L)).thenReturn(Optional.of(inventarioMock));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventarioMock);

        Inventario resultado = inventarioService.actualizarStockMinimo(10L, 8);

        assertEquals(8, resultado.getStockMinimo());
    }

    @Test
    void actualizarStockMinimo_Negativo_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> inventarioService.actualizarStockMinimo(10L, -1));
    }

    @Test
    void registrarMovimiento_EntradaSalidaYAjuste_ActualizanStock() {
        when(inventarioRepository.findByIdProductoForUpdate(10L)).thenReturn(Optional.of(inventarioMock));

        inventarioService.registrarMovimiento(new MovimientoRequest(10L, TipoMovimiento.ENTRADA, 10, "reposicion"));
        assertEquals(60, inventarioMock.getStockActual());

        inventarioService.registrarMovimiento(new MovimientoRequest(10L, TipoMovimiento.SALIDA, 20, "merma"));
        assertEquals(40, inventarioMock.getStockActual());

        inventarioService.registrarMovimiento(new MovimientoRequest(10L, TipoMovimiento.AJUSTE, 15, "conteo"));
        assertEquals(15, inventarioMock.getStockActual());

        verify(movimientoRepository, times(3)).save(any(MovimientoInventario.class));
    }

    @Test
    void registrarMovimiento_ErroresValidacion_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                inventarioService.registrarMovimiento(new MovimientoRequest(null, TipoMovimiento.ENTRADA, 5, "test")));

        when(inventarioRepository.findByIdProductoForUpdate(10L)).thenReturn(Optional.of(inventarioMock));

        assertThrows(IllegalStateException.class, () ->
                inventarioService.registrarMovimiento(new MovimientoRequest(10L, TipoMovimiento.SALIDA, 100, "exceso")));

        assertThrows(IllegalArgumentException.class, () ->
                inventarioService.registrarMovimiento(new MovimientoRequest(10L, TipoMovimiento.AJUSTE, -5, "negativo")));
    }

    @Test
    void descontar_StockSuficiente_DescuentaYRegistra() {
        when(inventarioRepository.findByIdProductoForUpdate(10L)).thenReturn(Optional.of(inventarioMock));
        DescuentoRequest req = new DescuentoRequest(100L, List.of(new ItemStockRequest(10L, 15)));

        inventarioService.descontar(req);

        assertEquals(35, inventarioMock.getStockActual());
        verify(movimientoRepository).save(any(MovimientoInventario.class));
    }

    @Test
    void descontar_StockInsuficiente_LanzaExcepcion() {
        when(inventarioRepository.findByIdProductoForUpdate(10L)).thenReturn(Optional.of(inventarioMock));
        Producto prod = new Producto();
        prod.setNombreProducto("Pan");
        when(productoRepository.findById(10L)).thenReturn(Optional.of(prod));

        DescuentoRequest req = new DescuentoRequest(100L, List.of(new ItemStockRequest(10L, 80)));

        assertThrows(IllegalStateException.class, () -> inventarioService.descontar(req));
    }

    @Test
    void descontar_SinItems_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> inventarioService.descontar(new DescuentoRequest(1L, List.of())));
    }

    @Test
    void reponer_Valido_IncrementaStock() {
        when(inventarioRepository.findByIdProductoForUpdate(10L)).thenReturn(Optional.of(inventarioMock));
        DescuentoRequest req = new DescuentoRequest(100L, List.of(new ItemStockRequest(10L, 10)));

        inventarioService.reponer(req);

        assertEquals(60, inventarioMock.getStockActual());
        verify(movimientoRepository).save(any(MovimientoInventario.class));
    }
}