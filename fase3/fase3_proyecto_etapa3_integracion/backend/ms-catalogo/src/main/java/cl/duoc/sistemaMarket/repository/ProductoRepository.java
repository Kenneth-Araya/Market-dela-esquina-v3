package cl.duoc.sistemaMarket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.sistemaMarket.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoProductoTrue();

    List<Producto> findByIdCategoriaAndActivoProductoTrue(Long idCategoria);

    long countByActivoProductoTrue();
}
