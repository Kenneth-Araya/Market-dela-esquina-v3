package cl.duoc.sistemaMarket.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.sistemaMarket.model.ItemCarrito;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    List<ItemCarrito> findByIdCarrito(Long idCarrito);

    Optional<ItemCarrito> findByIdCarritoAndIdProducto(Long idCarrito, Long idProducto);

    @Transactional
    void deleteByIdCarrito(Long idCarrito);
}
