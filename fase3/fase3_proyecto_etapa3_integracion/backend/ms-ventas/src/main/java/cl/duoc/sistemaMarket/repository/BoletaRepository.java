package cl.duoc.sistemaMarket.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.sistemaMarket.model.Boleta;

@Repository
public interface BoletaRepository extends JpaRepository<Boleta, Long> {

    Optional<Boleta> findByIdVenta(Long idVenta);
}
