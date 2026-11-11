package cl.duoc.sistemaMarket.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.sistemaMarket.model.TipoUsuario;
import cl.duoc.sistemaMarket.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreoUsuario(String correoUsuario);

    boolean existsByRunUsuario(String runUsuario);

    boolean existsByCorreoUsuario(String correoUsuario);

    long countByTipoUsuario(TipoUsuario tipoUsuario);
}
