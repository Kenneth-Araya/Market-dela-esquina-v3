package cl.duoc.sistemaMarket.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.sistemaMarket.client.CatalogoClient;
import cl.duoc.sistemaMarket.dto.DescuentoRequest;
import cl.duoc.sistemaMarket.dto.EstadisticasVentasDTO;
import cl.duoc.sistemaMarket.dto.ItemStockRequest;
import cl.duoc.sistemaMarket.dto.ProductoRemotoDTO;
import cl.duoc.sistemaMarket.dto.VentaDetalleDTO;
import cl.duoc.sistemaMarket.dto.VentaPorProductoDTO;
import cl.duoc.sistemaMarket.model.Boleta;
import cl.duoc.sistemaMarket.model.Carrito;
import cl.duoc.sistemaMarket.model.DetalleVenta;
import cl.duoc.sistemaMarket.model.EstadoCarrito;
import cl.duoc.sistemaMarket.model.EstadoVenta;
import cl.duoc.sistemaMarket.model.ItemCarrito;
import cl.duoc.sistemaMarket.model.Venta;
import cl.duoc.sistemaMarket.repository.BoletaRepository;
import cl.duoc.sistemaMarket.repository.CarritoRepository;
import cl.duoc.sistemaMarket.repository.DetalleVentaRepository;
import cl.duoc.sistemaMarket.repository.ItemCarritoRepository;
import cl.duoc.sistemaMarket.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final BoletaRepository boletaRepository;
    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final CatalogoClient catalogoClient;

    /**
     * "Finalizar venta": toma el carrito activo, recalcula el total con los precios reales del
     * catálogo, descuenta el stock, registra la venta, emite la boleta y cierra el carrito.
     */
    @Transactional
    public VentaDetalleDTO finalizarCompra(Long idUsuario) {
        Carrito carrito = carritoRepository.findByIdUsuarioAndEstadoCarrito(idUsuario, EstadoCarrito.ACTIVO)
                .orElseThrow(() -> new IllegalArgumentException("El carrito está vacío."));
        List<ItemCarrito> items = itemCarritoRepository.findByIdCarrito(carrito.getId());
        if (items.isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío.");
        }

        // 1. Detalles con nombre y precio reales (nunca los que mande el cliente)
        List<DetalleVenta> detalles = new ArrayList<>();
        List<ItemStockRequest> itemsStock = new ArrayList<>();
        int total = 0;
        for (ItemCarrito item : items) {
            ProductoRemotoDTO p = catalogoClient.obtenerProducto(item.getIdProducto());
            if (!Boolean.TRUE.equals(p.getActivoProducto())) {
                throw new IllegalStateException("\"" + p.getNombreProducto() + "\" ya no está disponible.");
            }
            DetalleVenta d = new DetalleVenta();
            d.setIdProducto(p.getId());
            d.setNombreProducto(p.getNombreProducto());
            d.setPrecioUnitarioDetalle(p.getPrecioProducto());
            d.setCantidadDetalle(item.getCantidadItem());
            d.setSubtotalDetalle(p.getPrecioProducto() * item.getCantidadItem());
            detalles.add(d);
            itemsStock.add(new ItemStockRequest(p.getId(), item.getCantidadItem()));
            total += d.getSubtotalDetalle();
        }

        // 2. Venta en PENDIENTE para obtener su N° (lo usa el movimiento de inventario)
        Venta venta = new Venta();
        venta.setIdUsuario(idUsuario);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setSubtotalVenta(total);
        venta.setTotalVenta(total);
        venta.setEstadoVenta(EstadoVenta.PENDIENTE);
        venta = ventaRepository.save(venta);

        // 3. Descontar stock (todo o nada). Si no alcanza, lanza IllegalStateException y se revierte la venta.
        DescuentoRequest descuento = new DescuentoRequest(venta.getId(), itemsStock);
        catalogoClient.descontarStock(descuento);

        // 4. Registrar detalles, boleta y cerrar carrito. Si algo falla, se devuelve el stock.
        try {
            for (DetalleVenta d : detalles) {
                d.setIdVenta(venta.getId());
            }
            detalleVentaRepository.saveAll(detalles);

            venta.setEstadoVenta(EstadoVenta.PAGADA);
            ventaRepository.save(venta);

            Boleta boleta = new Boleta();
            boleta.setIdVenta(venta.getId());
            boleta.setNumeroBoleta("B-" + String.format("%06d", venta.getId()));
            boleta.setFechaEmisionBoleta(LocalDateTime.now());
            boleta.setTotalBoleta(total);
            boletaRepository.save(boleta);

            carrito.setEstadoCarrito(EstadoCarrito.CONVERTIDO);
            carrito.setFechaActualizacionCarrito(LocalDateTime.now());
            carritoRepository.save(carrito);
        } catch (RuntimeException e) {
            try {
                catalogoClient.reponerStock(descuento);
            } catch (RuntimeException ex) {
                log.error("No se pudo devolver el stock de la venta {}", venta.getId(), ex);
            }
            throw e;
        }
        return new VentaDetalleDTO(venta, detalles);
    }

    public List<VentaDetalleDTO> listar() {
        return ventaRepository.findAllByOrderByFechaVentaDesc().stream().map(this::conDetalle).toList();
    }

    public List<VentaDetalleDTO> listarPorUsuario(Long idUsuario) {
        return ventaRepository.findByIdUsuarioOrderByFechaVentaDesc(idUsuario).stream().map(this::conDetalle).toList();
    }

    public VentaDetalleDTO buscarPorId(Long id) {
        return conDetalle(buscarVenta(id));
    }

    /** Anula una venta pagada y devuelve el stock al inventario. */
    @Transactional
    public VentaDetalleDTO anular(Long id) {
        Venta venta = buscarVenta(id);
        if (venta.getEstadoVenta() != EstadoVenta.PAGADA) {
            throw new IllegalStateException("Solo se pueden anular ventas pagadas.");
        }
        List<DetalleVenta> detalles = detalleVentaRepository.findByIdVenta(id);
        List<ItemStockRequest> itemsStock = detalles.stream()
                .map(d -> new ItemStockRequest(d.getIdProducto(), d.getCantidadDetalle()))
                .toList();
        catalogoClient.reponerStock(new DescuentoRequest(id, itemsStock));

        venta.setEstadoVenta(EstadoVenta.ANULADA);
        ventaRepository.save(venta);
        return new VentaDetalleDTO(venta, detalles);
    }

    public EstadisticasVentasDTO estadisticas() {
        long ventas = ventaRepository.countByEstadoVenta(EstadoVenta.PAGADA);
        long ingresos = ventaRepository.sumarTotalPorEstado(EstadoVenta.PAGADA);
        long ticket = ventas == 0 ? 0 : ingresos / ventas;
        LocalDateTime desde = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime hasta = desde.plusMonths(1);
        return new EstadisticasVentasDTO(
                ventas,
                ingresos,
                ticket,
                ventaRepository.ultimaFechaPorEstado(EstadoVenta.PAGADA),
                ventaRepository.contarPorEstadoEntre(EstadoVenta.PAGADA, desde, hasta),
                ventaRepository.sumarTotalPorEstadoEntre(EstadoVenta.PAGADA, desde, hasta));
    }

    public List<VentaPorProductoDTO> ventasPorProducto() {
        return detalleVentaRepository.ventasPorProducto(EstadoVenta.PAGADA);
    }

    private Venta buscarVenta(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Venta no encontrada: " + id));
    }

    private VentaDetalleDTO conDetalle(Venta venta) {
        return new VentaDetalleDTO(venta, detalleVentaRepository.findByIdVenta(venta.getId()));
    }
}
