package cl.duoc.sistemaMarket.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.sistemaMarket.model.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    Optional<Region> findByNombreRegion(String nombreRegion);
}
