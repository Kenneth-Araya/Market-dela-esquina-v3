package cl.duoc.sistemaMarket.service;

import cl.duoc.sistemaMarket.client.CatalogoClient;
import cl.duoc.sistemaMarket.dto.EstadisticasVentasDTO;
import cl.duoc.sistemaMarket.dto.ProductoRemotoDTO;
import cl.duoc.sistemaMarket.dto.VentaDetalleDTO;
import cl.duoc.sistemaMarket.dto.VentaPorProductoDTO;
import cl.duoc.sistemaMarket.model.*;
import cl.duoc.sistemaMarket.repository.*;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @Mock
    private BoletaRepository boletaRepository;

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ItemCarritoRepository itemCarritoRepository;

    @Mock
    private CatalogoClient catalogoClient;

    @InjectMocks
    private VentaService ventaService;

    private Carrito carritoMock;
    private ProductoRemotoDTO productoMock;
    private Venta ventaMock;

    @BeforeEach
    void setUp() {
        carritoMock = new Carrito(1L, 5L, EstadoCarrito.ACTIVO, LocalDateTime.now());
        productoMock = new ProductoRemotoDTO();
        productoMock.setId(10L);
        productoMock.setNombreProducto("Pan");
        productoMock.setPrecioProducto(1500);
        productoMock.setActivoProducto(true);

        ventaMock = new Venta(1L, 5L, LocalDateTime.now(), 3000, 3000, EstadoVenta.PAGADA);
    }

    @Test
    void finalizarCompra_Exitoso_RegistraVentaBoletaYCierraCarrito() {
        ItemCarrito item = new ItemCarrito(1L, 1L, 10L, 2);
        when(carritoRepository.findByIdUsuarioAndEstadoCarrito(5L, EstadoCarrito.ACTIVO)).thenReturn(Optional.of(carritoMock));
        when(itemCarritoRepository.findByIdCarrito(1L)).thenReturn(List.of(item));
        when(catalogoClient.obtenerProducto(10L)).thenReturn(productoMock);
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> {
            Venta v = inv.getArgument(0);
            v.setId(1L);
            return v;
        });

        VentaDetalleDTO resultado = ventaService.finalizarCompra(5L);

        assertEquals(EstadoVenta.PAGADA, resultado.getVenta().getEstadoVenta());
        assertEquals(3000, resultado.getVenta().getTotalVenta());
        assertEquals(EstadoCarrito.CONVERTIDO, carritoMock.getEstadoCarrito());
        verify(catalogoClient).descontarStock(any());
        verify(boletaRepository).save(any(Boleta.class));
    }

    @Test
    void finalizarCompra_FallaAlGuardarDetalles_ReponeStockYRelanza() {
        ItemCarrito item = new ItemCarrito(1L, 1L, 10L, 2);
        when(carritoRepository.findByIdUsuarioAndEstadoCarrito(5L, EstadoCarrito.ACTIVO)).thenReturn(Optional.of(carritoMock));
        when(itemCarritoRepository.findByIdCarrito(1L)).thenReturn(List.of(item));
        when(catalogoClient.obtenerProducto(10L)).thenReturn(productoMock);
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> {
            Venta v = inv.getArgument(0);
            v.setId(1L);
            return v;
        });
        when(detalleVentaRepository.saveAll(any())).thenThrow(new RuntimeException("Error BD"));

        assertThrows(RuntimeException.class, () -> ventaService.finalizarCompra(5L));
        verify(catalogoClient).reponerStock(any());
    }

    @Test
    void finalizarCompra_CarritoVacioOProductoInactivo_LanzaExcepcion() {
        when(carritoRepository.findByIdUsuarioAndEstadoCarrito(5L, EstadoCarrito.ACTIVO)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> ventaService.finalizarCompra(5L));

        when(carritoRepository.findByIdUsuarioAndEstadoCarrito(5L, EstadoCarrito.ACTIVO)).thenReturn(Optional.of(carritoMock));
        when(itemCarritoRepository.findByIdCarrito(1L)).thenReturn(List.of());
        assertThrows(IllegalArgumentException.class, () -> ventaService.finalizarCompra(5L));

        ItemCarrito item = new ItemCarrito(1L, 1L, 10L, 1);
        productoMock.setActivoProducto(false);
        when(itemCarritoRepository.findByIdCarrito(1L)).thenReturn(List.of(item));
        when(catalogoClient.obtenerProducto(10L)).thenReturn(productoMock);
        assertThrows(IllegalStateException.class, () -> ventaService.finalizarCompra(5L));
    }

    @Test
    void listarYBuscar_RetornanVentasConDetalle() {
        when(ventaRepository.findAllByOrderByFechaVentaDesc()).thenReturn(List.of(ventaMock));
        when(ventaRepository.findByIdUsuarioOrderByFechaVentaDesc(5L)).thenReturn(List.of(ventaMock));
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(ventaMock));
        when(detalleVentaRepository.findByIdVenta(1L)).thenReturn(List.of());

        assertEquals(1, ventaService.listar().size());
        assertEquals(1, ventaService.listarPorUsuario(5L).size());
        assertEquals(1L, ventaService.buscarPorId(1L).getVenta().getId());
    }

    @Test
    void buscarPorId_NoExiste_LanzaExcepcion() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> ventaService.buscarPorId(99L));
    }

    @Test
    void anular_VentaPagada_AnulaYReponeStock() {
        DetalleVenta det = new DetalleVenta(1L, 1L, 10L, "Pan", 1500, 2, 3000);
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(ventaMock));
        when(detalleVentaRepository.findByIdVenta(1L)).thenReturn(List.of(det));

        VentaDetalleDTO resultado = ventaService.anular(1L);

        assertEquals(EstadoVenta.ANULADA, resultado.getVenta().getEstadoVenta());
        verify(catalogoClient).reponerStock(any());
    }

    @Test
    void anular_VentaYaAnulada_LanzaExcepcion() {
        ventaMock.setEstadoVenta(EstadoVenta.ANULADA);
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(ventaMock));

        assertThrows(IllegalStateException.class, () -> ventaService.anular(1L));
    }

    @Test
    void estadisticasYReportes_RetornanMetricas() {
        when(ventaRepository.countByEstadoVenta(EstadoVenta.PAGADA)).thenReturn(4L);
        when(ventaRepository.sumarTotalPorEstado(EstadoVenta.PAGADA)).thenReturn(20000L);
        when(ventaRepository.ultimaFechaPorEstado(EstadoVenta.PAGADA)).thenReturn(LocalDateTime.now());
        when(ventaRepository.contarPorEstadoEntre(eq(EstadoVenta.PAGADA), any(), any())).thenReturn(2L);
        when(ventaRepository.sumarTotalPorEstadoEntre(eq(EstadoVenta.PAGADA), any(), any())).thenReturn(10000L);
        when(detalleVentaRepository.ventasPorProducto(EstadoVenta.PAGADA))
                .thenReturn(List.of(new VentaPorProductoDTO(10L, "Pan", 10L, 15000L)));

        EstadisticasVentasDTO stats = ventaService.estadisticas();
        List<VentaPorProductoDTO> porProd = ventaService.ventasPorProducto();

        assertEquals(5000L, stats.getTicketPromedio());
        assertEquals(1, porProd.size());
    }
}