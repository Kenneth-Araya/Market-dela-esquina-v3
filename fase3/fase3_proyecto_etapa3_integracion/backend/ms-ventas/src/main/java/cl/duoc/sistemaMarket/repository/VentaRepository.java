package cl.duoc.sistemaMarket.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cl.duoc.sistemaMarket.model.EstadoVenta;
import cl.duoc.sistemaMarket.model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findAllByOrderByFechaVentaDesc();

    List<Venta> findByIdUsuarioOrderByFechaVentaDesc(Long idUsuario);

    long countByEstadoVenta(EstadoVenta estadoVenta);

    @Query("select coalesce(sum(v.totalVenta), 0) from Venta v where v.estadoVenta = :estado")
    Long sumarTotalPorEstado(@Param("estado") EstadoVenta estado);

    @Query("select max(v.fechaVenta) from Venta v where v.estadoVenta = :estado")
    LocalDateTime ultimaFechaPorEstado(@Param("estado") EstadoVenta estado);

    @Query("select count(v) from Venta v where v.estadoVenta = :estado "
            + "and v.fechaVenta >= :desde and v.fechaVenta < :hasta")
    long contarPorEstadoEntre(@Param("estado") EstadoVenta estado,
                              @Param("desde") LocalDateTime desde,
                              @Param("hasta") LocalDateTime hasta);

    @Query("select coalesce(sum(v.totalVenta), 0) from Venta v where v.estadoVenta = :estado "
            + "and v.fechaVenta >= :desde and v.fechaVenta < :hasta")
    Long sumarTotalPorEstadoEntre(@Param("estado") EstadoVenta estado,
                                  @Param("desde") LocalDateTime desde,
                                  @Param("hasta") LocalDateTime hasta);
}
