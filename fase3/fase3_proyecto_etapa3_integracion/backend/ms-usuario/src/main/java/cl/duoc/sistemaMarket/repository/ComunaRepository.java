package cl.duoc.sistemaMarket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.sistemaMarket.model.Comuna;

@Repository
public interface ComunaRepository extends JpaRepository<Comuna, Long> {

    List<Comuna> findByIdRegion(Long idRegion);

    boolean existsByNombreComunaAndIdRegion(String nombreComuna, Long idRegion);
}
