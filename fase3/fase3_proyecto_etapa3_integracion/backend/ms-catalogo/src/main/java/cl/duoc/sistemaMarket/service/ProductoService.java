package cl.duoc.sistemaMarket.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.sistemaMarket.model.Inventario;
import cl.duoc.sistemaMarket.model.Producto;
import cl.duoc.sistemaMarket.repository.CategoriaRepository;
import cl.duoc.sistemaMarket.repository.InventarioRepository;
import cl.duoc.sistemaMarket.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final InventarioRepository inventarioRepository;

    /** Catálogo visible para clientes (solo activos) o completo para el admin. */
    public List<Producto> listar(boolean incluirInactivos, Long idCategoria) {
        if (incluirInactivos) {
            return productoRepository.findAll();
        }
        if (idCategoria != null) {
            return productoRepository.findByIdCategoriaAndActivoProductoTrue(idCategoria);
        }
        return productoRepository.findByActivoProductoTrue();
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + id));
    }

    public long totalActivos() {
        return productoRepository.countByActivoProductoTrue();
    }

    @Transactional
    public Producto crear(Producto producto) {
        validar(producto);
        producto.setId(null);
        if (producto.getActivoProducto() == null) {
            producto.setActivoProducto(true);
        }
        Producto guardado = productoRepository.save(producto);

        // Todo producto nace con su registro de inventario en cero
        Inventario inventario = new Inventario();
        inventario.setIdProducto(guardado.getId());
        inventario.setStockActual(0);
        inventario.setStockMinimo(0);
        inventarioRepository.save(inventario);
        return guardado;
    }

    @Transactional
    public Producto actualizar(Long id, Producto datos) {
        Producto actual = buscarPorId(id);
        validar(datos);
        actual.setNombreProducto(datos.getNombreProducto());
        actual.setDescripcionProducto(datos.getDescripcionProducto());
        actual.setPrecioProducto(datos.getPrecioProducto());
        actual.setPesoProducto(datos.getPesoProducto());
        actual.setImagenProducto(datos.getImagenProducto());
        actual.setRatingProducto(datos.getRatingProducto());
        actual.setIdCategoria(datos.getIdCategoria());
        if (datos.getActivoProducto() != null) {
            actual.setActivoProducto(datos.getActivoProducto());
        }
        return productoRepository.save(actual);
    }

    /** Baja lógica: el producto deja de mostrarse pero se conserva el historial de ventas. */
    @Transactional
    public void desactivar(Long id) {
        Producto producto = buscarPorId(id);
        producto.setActivoProducto(false);
        productoRepository.save(producto);
    }

    private void validar(Producto p) {
        if (p.getNombreProducto() == null || p.getNombreProducto().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        p.setNombreProducto(p.getNombreProducto().trim());
        if (p.getNombreProducto().length() > 150) {
            throw new IllegalArgumentException("El nombre del producto no puede superar los 150 caracteres.");
        }
        if (p.getDescripcionProducto() != null && p.getDescripcionProducto().length() > 1000) {
            throw new IllegalArgumentException("La descripción no puede superar los 1000 caracteres.");
        }
        if (p.getPrecioProducto() == null || p.getPrecioProducto() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
        if (p.getRatingProducto() != null && (p.getRatingProducto() < 0 || p.getRatingProducto() > 5)) {
            throw new IllegalArgumentException("El rating debe estar entre 0 y 5.");
        }
        if (p.getIdCategoria() != null && !categoriaRepository.existsById(p.getIdCategoria())) {
            throw new IllegalArgumentException("La categoría indicada no existe.");
        }
    }
}
