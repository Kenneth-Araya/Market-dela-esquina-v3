package cl.duoc.sistemaMarket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cl.duoc.sistemaMarket.model.Boleta;

@Repository
public interface BoletaRepository extends JpaRepository<Boleta, Long> {

    Boleta findByFolioBoleta(String folio);

    void deleteByFolioBoleta(String folio);

    List<Boleta> findAllByOrderByIdAsc();

    boolean existsByFolioBoleta(String folioBoleta);
}
