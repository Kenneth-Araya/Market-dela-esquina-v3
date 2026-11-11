package cl.duoc.sistemaMarket.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

import cl.duoc.sistemaMarket.model.Inventario;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByIdProducto(Long idProducto);

    // Bloquea la fila para que dos compras simultaneas no vendan el mismo stock dos veces
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventario i where i.idProducto = :idProducto")
    Optional<Inventario> findByIdProductoForUpdate(@Param("idProducto") Long idProducto);
}
