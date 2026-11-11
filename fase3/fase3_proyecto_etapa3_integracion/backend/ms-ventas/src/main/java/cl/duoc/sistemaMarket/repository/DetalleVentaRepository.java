package cl.duoc.sistemaMarket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cl.duoc.sistemaMarket.dto.VentaPorProductoDTO;
import cl.duoc.sistemaMarket.model.DetalleVenta;
import cl.duoc.sistemaMarket.model.EstadoVenta;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    List<DetalleVenta> findByIdVenta(Long idVenta);

    @Query("select new cl.duoc.sistemaMarket.dto.VentaPorProductoDTO("
            + "d.idProducto, d.nombreProducto, sum(d.cantidadDetalle), sum(d.subtotalDetalle)) "
            + "from DetalleVenta d, Venta v "
            + "where d.idVenta = v.id and v.estadoVenta = :estado "
            + "group by d.idProducto, d.nombreProducto "
            + "order by sum(d.cantidadDetalle) desc")
    List<VentaPorProductoDTO> ventasPorProducto(@Param("estado") EstadoVenta estado);
}
