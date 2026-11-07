package cl.duoc.msVenta.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cl.duoc.msVenta.model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    Optional<Venta> findByIdVenta(Long idVenta);
    
    void deleteByIdVenta(Long idVenta);

    List<Venta> findAllByOrderByIdVentaAsc();

    boolean existsByDescripcionVenta(String descripcionVenta);
}