package cl.duoc.sistemaMarket.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cl.duoc.sistemaMarket.model.Categoria;
import cl.duoc.sistemaMarket.model.Inventario;
import cl.duoc.sistemaMarket.model.Producto;
import cl.duoc.sistemaMarket.repository.CategoriaRepository;
import cl.duoc.sistemaMarket.repository.InventarioRepository;
import cl.duoc.sistemaMarket.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DatosIniciales implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;

    @Override
    public void run(String... args) {
        if (productoRepository.count() > 0) {
            return;
        }
        Long panaderia = categoria("Panadería");
        Long lacteos = categoria("Lácteos");
        Long verduras = categoria("Frutas y verduras");
        Long despensa = categoria("Despensa");

        // Catálogo unificado: reemplaza los dos arreglos distintos que tenía el frontend
        producto("Pan amasado casero", "Pan amasado casero, recién horneado.", 1500, "500gm", "img/pan.jpg", 4.8, panaderia);
        producto("Queso gauda laminado", "Queso gauda laminado.", 3200, "250gm", "img/queso.jpg", 4.7, lacteos);
        producto("Palta hass madura", "Palta hass lista para consumir.", 990, "1 unidad", "img/palta.jpg", 4.8, verduras);
        producto("Leche entera Colun", "Leche entera de excelente calidad nutritiva.", 1190, "1L", "img/leche.jpg", 4.6, lacteos);
        producto("Fideos San Remo", "Pasta Tallarines 5 de San Remo.", 800, "400gm", "img/fideos.jpg", 4.5, despensa);
        producto("Lomito de atún", "Atún en agua Cuisine & Co, ideal para comidas livianas.", 1700, "170gm", "img/lomito.jpg", 4.4, despensa);
    }

    private Long categoria(String nombre) {
        Categoria c = new Categoria();
        c.setNombreCategoria(nombre);
        return categoriaRepository.save(c).getId();
    }

    private void producto(String nombre, String descripcion, int precio, String peso,
                          String imagen, double rating, Long idCategoria) {
        Producto p = new Producto();
        p.setNombreProducto(nombre);
        p.setDescripcionProducto(descripcion);
        p.setPrecioProducto(precio);
        p.setPesoProducto(peso);
        p.setImagenProducto(imagen);
        p.setRatingProducto(rating);
        p.setActivoProducto(true);
        p.setIdCategoria(idCategoria);
        p = productoRepository.save(p);

        Inventario inv = new Inventario();
        inv.setIdProducto(p.getId());
        inv.setStockActual(50);
        inv.setStockMinimo(5);
        inventarioRepository.save(inv);
    }
}
