package cl.duoc.ms_producto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.ms_producto.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>  {
    // Spring generará un "ORDER BY id ASC" automáticamente
    List<Producto> findAllByOrderByIdAsc();
    boolean existsByNombre(String nombre);
}
