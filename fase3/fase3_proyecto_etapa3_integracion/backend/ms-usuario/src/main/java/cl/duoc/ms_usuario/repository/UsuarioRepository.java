package cl.duoc.sistemaMarket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cl.duoc.sistemaMarket.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Usuario findById(Long id);

    void deleteById(Long id);

    List<Usuario> findAllByOrderByIdAsc();

    boolean existsByNombreUsuario(String nombreUsuario);
}