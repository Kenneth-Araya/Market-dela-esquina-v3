package cl.duoc.sistemaMarket.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.sistemaMarket.model.Carrito;
import cl.duoc.sistemaMarket.model.EstadoCarrito;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByIdUsuarioAndEstadoCarrito(Long idUsuario, EstadoCarrito estadoCarrito);
}
