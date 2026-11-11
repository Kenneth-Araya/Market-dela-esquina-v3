package cl.duoc.sistemaMarket.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.sistemaMarket.dto.DescuentoRequest;
import cl.duoc.sistemaMarket.dto.ItemStockRequest;
import cl.duoc.sistemaMarket.dto.MovimientoRequest;
import cl.duoc.sistemaMarket.model.Inventario;
import cl.duoc.sistemaMarket.model.MovimientoInventario;
import cl.duoc.sistemaMarket.model.TipoMovimiento;
import cl.duoc.sistemaMarket.repository.InventarioRepository;
import cl.duoc.sistemaMarket.repository.MovimientoInventarioRepository;
import cl.duoc.sistemaMarket.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;

    public List<Inventario> listar() {
        return inventarioRepository.findAll();
    }

    public Inventario obtener(Long idProducto) {
        return inventarioRepository.findByIdProducto(idProducto)
                .orElseThrow(() -> new NoSuchElementException("Producto sin inventario: " + idProducto));
    }

    public List<MovimientoInventario> movimientos(Long idProducto) {
        return movimientoRepository.findByIdProductoOrderByFechaMovimientoDesc(idProducto);
    }

    @Transactional
    public Inventario actualizarStockMinimo(Long idProducto, Integer stockMinimo) {
        if (stockMinimo == null || stockMinimo < 0) {
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo.");
        }
        Inventario inventario = obtener(idProducto);
        inventario.setStockMinimo(stockMinimo);
        return inventarioRepository.save(inventario);
    }

    /** Movimiento manual (reposición, merma, conteo físico). */
    @Transactional
    public Inventario registrarMovimiento(MovimientoRequest req) {
        if (req.getIdProducto() == null || req.getTipoMovimiento() == null || req.getCantidad() == null) {
            throw new IllegalArgumentException("Producto, tipo de movimiento y cantidad son obligatorios.");
        }
        Inventario inventario = bloquear(req.getIdProducto());
        int cantidad = req.getCantidad();

        switch (req.getTipoMovimiento()) {
            case ENTRADA -> {
                exigirPositiva(cantidad);
                inventario.setStockActual(inventario.getStockActual() + cantidad);
            }
            case SALIDA -> {
                exigirPositiva(cantidad);
                if (inventario.getStockActual() < cantidad) {
                    throw new IllegalStateException("No hay stock suficiente para registrar la salida.");
                }
                inventario.setStockActual(inventario.getStockActual() - cantidad);
            }
            case AJUSTE -> {
                if (cantidad < 0) {
                    throw new IllegalArgumentException("El stock ajustado no puede ser negativo.");
                }
                inventario.setStockActual(cantidad);
            }
        }
        inventarioRepository.save(inventario);
        guardarMovimiento(req.getIdProducto(), req.getTipoMovimiento(), cantidad, req.getMotivo(), null);
        return inventario;
    }

    /**
     * Descuenta el stock de todos los items de una venta. Es todo o nada:
     * si un solo producto no tiene stock, se lanza IllegalStateException y se revierte todo.
     */
    @Transactional
    public void descontar(DescuentoRequest req) {
        for (ItemStockRequest item : ordenar(req)) {
            exigirPositiva(item.getCantidad());
            Inventario inventario = bloquear(item.getIdProducto());
            if (inventario.getStockActual() < item.getCantidad()) {
                String nombre = productoRepository.findById(item.getIdProducto())
                        .map(p -> p.getNombreProducto()).orElse("id " + item.getIdProducto());
                throw new IllegalStateException("Stock insuficiente para \"" + nombre
                        + "\" (disponible: " + inventario.getStockActual() + ").");
            }
            inventario.setStockActual(inventario.getStockActual() - item.getCantidad());
            inventarioRepository.save(inventario);
            guardarMovimiento(item.getIdProducto(), TipoMovimiento.SALIDA, item.getCantidad(),
                    "Venta #" + req.getIdVenta(), req.getIdVenta());
        }
    }

    /** Devuelve el stock (venta anulada o compensación si falló el registro de la venta). */
    @Transactional
    public void reponer(DescuentoRequest req) {
        for (ItemStockRequest item : ordenar(req)) {
            exigirPositiva(item.getCantidad());
            Inventario inventario = bloquear(item.getIdProducto());
            inventario.setStockActual(inventario.getStockActual() + item.getCantidad());
            inventarioRepository.save(inventario);
            guardarMovimiento(item.getIdProducto(), TipoMovimiento.ENTRADA, item.getCantidad(),
                    "Anulación de venta #" + req.getIdVenta(), req.getIdVenta());
        }
    }

    // ---------------- helpers ----------------

    // Se ordena por id de producto para bloquear siempre en el mismo orden y evitar deadlocks
    private List<ItemStockRequest> ordenar(DescuentoRequest req) {
        if (req == null || req.getItems() == null || req.getItems().isEmpty()) {
            throw new IllegalArgumentException("La solicitud no tiene productos.");
        }
        return req.getItems().stream()
                .sorted(Comparator.comparing(ItemStockRequest::getIdProducto))
                .toList();
    }

    private Inventario bloquear(Long idProducto) {
        return inventarioRepository.findByIdProductoForUpdate(idProducto)
                .orElseThrow(() -> new NoSuchElementException("Producto sin inventario: " + idProducto));
    }

    private void exigirPositiva(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }
    }

    private void guardarMovimiento(Long idProducto, TipoMovimiento tipo, int cantidad, String motivo, Long idVenta) {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setIdProducto(idProducto);
        mov.setTipoMovimiento(tipo);
        mov.setCantidadMovimiento(cantidad);
        mov.setMotivoMovimiento(motivo);
        mov.setIdVenta(idVenta);
        mov.setFechaMovimiento(LocalDateTime.now());
        movimientoRepository.save(mov);
    }
}
