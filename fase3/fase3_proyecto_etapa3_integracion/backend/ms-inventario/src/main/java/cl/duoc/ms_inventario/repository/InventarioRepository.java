package cl.duoc.ms_inventario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import cl.duoc.ms_inventario.model.Inventario;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long >{
    // Método CLAVE: Para buscar el stock de un producto específico
    // Recuerda que en tu Boleta usas el idProducto, no el idInventario.
    Optional<Inventario> findByIdProducto(Long idProducto);
    // Buscamos todos los registros donde el stock actual sea menor o igual al mínimo
    @Query("SELECT i FROM Inventario i WHERE i.stockActual <= i.stockMinimo")
    List<Inventario> findProductosBajoStock();
}
