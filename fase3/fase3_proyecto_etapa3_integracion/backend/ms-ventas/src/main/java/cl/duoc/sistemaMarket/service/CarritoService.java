package cl.duoc.sistemaMarket.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.sistemaMarket.client.CatalogoClient;
import cl.duoc.sistemaMarket.dto.AgregarItemRequest;
import cl.duoc.sistemaMarket.dto.CarritoDTO;
import cl.duoc.sistemaMarket.dto.ItemCarritoDTO;
import cl.duoc.sistemaMarket.dto.ProductoRemotoDTO;
import cl.duoc.sistemaMarket.model.Carrito;
import cl.duoc.sistemaMarket.model.EstadoCarrito;
import cl.duoc.sistemaMarket.model.ItemCarrito;
import cl.duoc.sistemaMarket.repository.CarritoRepository;
import cl.duoc.sistemaMarket.repository.ItemCarritoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final CatalogoClient catalogoClient;

    @Transactional
    public CarritoDTO obtener(Long idUsuario) {
        return armarDTO(obtenerOCrear(idUsuario));
    }

    @Transactional
    public CarritoDTO agregarItem(Long idUsuario, AgregarItemRequest request) {
        if (request.getIdProducto() == null) {
            throw new IllegalArgumentException("El producto es obligatorio.");
        }
        int cantidad = request.getCantidad() == null ? 1 : request.getCantidad();
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }

        ProductoRemotoDTO producto = catalogoClient.obtenerProducto(request.getIdProducto());
        if (!Boolean.TRUE.equals(producto.getActivoProducto())) {
            throw new IllegalArgumentException("El producto no está disponible.");
        }

        Carrito carrito = obtenerOCrear(idUsuario);
        ItemCarrito item = itemCarritoRepository
                .findByIdCarritoAndIdProducto(carrito.getId(), request.getIdProducto())
                .orElseGet(() -> {
                    ItemCarrito nuevo = new ItemCarrito();
                    nuevo.setIdCarrito(carrito.getId());
                    nuevo.setIdProducto(request.getIdProducto());
                    nuevo.setCantidadItem(0);
                    return nuevo;
                });
        item.setCantidadItem(item.getCantidadItem() + cantidad);
        itemCarritoRepository.save(item);
        return tocarYArmar(carrito);
    }

    @Transactional
    public CarritoDTO modificarCantidad(Long idUsuario, Long idProducto, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0. Para quitar el producto usa DELETE.");
        }
        Carrito carrito = obtenerOCrear(idUsuario);
        ItemCarrito item = itemCarritoRepository.findByIdCarritoAndIdProducto(carrito.getId(), idProducto)
                .orElseThrow(() -> new NoSuchElementException("El producto no está en el carrito."));
        item.setCantidadItem(cantidad);
        itemCarritoRepository.save(item);
        return tocarYArmar(carrito);
    }

    @Transactional
    public CarritoDTO quitarItem(Long idUsuario, Long idProducto) {
        Carrito carrito = obtenerOCrear(idUsuario);
        ItemCarrito item = itemCarritoRepository.findByIdCarritoAndIdProducto(carrito.getId(), idProducto)
                .orElseThrow(() -> new NoSuchElementException("El producto no está en el carrito."));
        itemCarritoRepository.delete(item);
        return tocarYArmar(carrito);
    }

    @Transactional
    public CarritoDTO vaciar(Long idUsuario) {
        Carrito carrito = obtenerOCrear(idUsuario);
        itemCarritoRepository.deleteByIdCarrito(carrito.getId());
        return tocarYArmar(carrito);
    }

    // ---------------- helpers ----------------

    private Carrito obtenerOCrear(Long idUsuario) {
        return carritoRepository.findByIdUsuarioAndEstadoCarrito(idUsuario, EstadoCarrito.ACTIVO)
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setIdUsuario(idUsuario);
                    nuevo.setEstadoCarrito(EstadoCarrito.ACTIVO);
                    nuevo.setFechaActualizacionCarrito(LocalDateTime.now());
                    return carritoRepository.save(nuevo);
                });
    }

    private CarritoDTO tocarYArmar(Carrito carrito) {
        carrito.setFechaActualizacionCarrito(LocalDateTime.now());
        carritoRepository.save(carrito);
        return armarDTO(carrito);
    }

    private CarritoDTO armarDTO(Carrito carrito) {
        List<ItemCarritoDTO> items = new ArrayList<>();
        int subtotal = 0;
        for (ItemCarrito item : itemCarritoRepository.findByIdCarrito(carrito.getId())) {
            ProductoRemotoDTO p = catalogoClient.obtenerProducto(item.getIdProducto());
            int subtotalItem = p.getPrecioProducto() * item.getCantidadItem();
            subtotal += subtotalItem;
            items.add(new ItemCarritoDTO(p.getId(), p.getNombreProducto(), p.getImagenProducto(),
                    p.getPesoProducto(), p.getPrecioProducto(), item.getCantidadItem(), subtotalItem));
        }
        return new CarritoDTO(carrito.getId(), carrito.getIdUsuario(), items, subtotal, subtotal);
    }
}
