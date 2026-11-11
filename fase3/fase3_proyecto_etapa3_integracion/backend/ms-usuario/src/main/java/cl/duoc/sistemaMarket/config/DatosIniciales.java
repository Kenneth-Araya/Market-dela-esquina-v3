package cl.duoc.sistemaMarket.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import cl.duoc.sistemaMarket.model.Comuna;
import cl.duoc.sistemaMarket.model.EstadoUsuario;
import cl.duoc.sistemaMarket.model.Region;
import cl.duoc.sistemaMarket.model.TipoUsuario;
import cl.duoc.sistemaMarket.model.Usuario;
import cl.duoc.sistemaMarket.repository.ComunaRepository;
import cl.duoc.sistemaMarket.repository.RegionRepository;
import cl.duoc.sistemaMarket.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

//@Component
@RequiredArgsConstructor
public class DatosIniciales implements CommandLineRunner {

    private final RegionRepository regionRepository;
    private final ComunaRepository comunaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (regionRepository.count() == 0) {
            crearRegion("Región Metropolitana", List.of("Santiago", "Maipú", "Puente Alto", "Las Condes"));
            crearRegion("Valparaíso", List.of("Valparaíso", "Viña del Mar", "Quilpué"));
        }
        // Administrador inicial para poder probar el login (cambiar la clave en producción)
        if (!usuarioRepository.existsByCorreoUsuario("admin@duoc.cl")) {
            Usuario admin = new Usuario();
            admin.setRunUsuario("111111111");
            admin.setNombreUsuario("Admin");
            admin.setApellidosUsuario("Market");
            admin.setCorreoUsuario("admin@duoc.cl");
            admin.setTipoUsuario(TipoUsuario.ADMINISTRADOR);
            admin.setRegionUsuario("Valparaíso");
            admin.setComunaUsuario("Quilpué");
            admin.setDireccionUsuario("Local central");
            admin.setContrasenaUsuario(passwordEncoder.encode("admin123"));
            admin.setEstadoUsuario(EstadoUsuario.ACTIVO);
            admin.setFechaRegistroUsuario(LocalDateTime.now());
            usuarioRepository.save(admin);
        }
    }

    private void crearRegion(String nombre, List<String> comunas) {
        Region region = new Region();
        region.setNombreRegion(nombre);
        region = regionRepository.save(region);
        for (String nombreComuna : comunas) {
            Comuna comuna = new Comuna();
            comuna.setNombreComuna(nombreComuna);
            comuna.setIdRegion(region.getId());
            comunaRepository.save(comuna);
        }
    }
}
