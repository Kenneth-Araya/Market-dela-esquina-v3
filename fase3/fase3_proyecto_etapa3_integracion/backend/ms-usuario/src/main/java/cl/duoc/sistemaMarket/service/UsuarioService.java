package cl.duoc.sistemaMarket.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.sistemaMarket.dto.EstadisticasUsuarioDTO;
import cl.duoc.sistemaMarket.model.EstadoUsuario;
import cl.duoc.sistemaMarket.model.Region;
import cl.duoc.sistemaMarket.model.TipoUsuario;
import cl.duoc.sistemaMarket.model.Usuario;
import cl.duoc.sistemaMarket.repository.ComunaRepository;
import cl.duoc.sistemaMarket.repository.RegionRepository;
import cl.duoc.sistemaMarket.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final List<String> DOMINIOS_PERMITIDOS = List.of("@duoc.cl", "@profesor.duoc.cl", "@gmail.com");

    private final UsuarioRepository usuarioRepository;
    private final RegionRepository regionRepository;
    private final ComunaRepository comunaRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado: " + id));
    }

    @Transactional
    public Usuario registrar(Usuario usuario) {

        // SEGURIDAD: Los registros públicos siempre serán CLIENTES.
        // Nadie puede registrarse como ADMINISTRADOR desde la tienda.
        usuario.setTipoUsuario(TipoUsuario.CLIENTE);

        normalizar(usuario);
        validar(usuario, true);

        if (usuarioRepository.existsByRunUsuario(usuario.getRunUsuario())) {
            throw new IllegalStateException("Ya existe un usuario con ese RUN.");
        }

        if (usuarioRepository.existsByCorreoUsuario(usuario.getCorreoUsuario())) {
            throw new IllegalStateException("Ya existe un usuario con ese correo.");
        }

        usuario.setId(null);
        usuario.setEstadoUsuario(EstadoUsuario.ACTIVO);
        usuario.setFechaRegistroUsuario(LocalDateTime.now());

        // Ciframos la contraseña antes de guardarla.
        usuario.setContrasenaUsuario(
                passwordEncoder.encode(usuario.getContrasenaUsuario()));

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizar(Long id, Usuario datos) {
        Usuario actual = buscarPorId(id);
        normalizar(datos);
        validar(datos, false);

        if (!actual.getRunUsuario().equals(datos.getRunUsuario())
                && usuarioRepository.existsByRunUsuario(datos.getRunUsuario())) {
            throw new IllegalStateException("Ya existe un usuario con ese RUN.");
        }
        if (!actual.getCorreoUsuario().equals(datos.getCorreoUsuario())
                && usuarioRepository.existsByCorreoUsuario(datos.getCorreoUsuario())) {
            throw new IllegalStateException("Ya existe un usuario con ese correo.");
        }

        actual.setRunUsuario(datos.getRunUsuario());
        actual.setNombreUsuario(datos.getNombreUsuario());
        actual.setApellidosUsuario(datos.getApellidosUsuario());
        actual.setCorreoUsuario(datos.getCorreoUsuario());
        actual.setFechaNacimientoUsuario(datos.getFechaNacimientoUsuario());
        actual.setTipoUsuario(datos.getTipoUsuario());
        actual.setRegionUsuario(datos.getRegionUsuario());
        actual.setComunaUsuario(datos.getComunaUsuario());
        actual.setDireccionUsuario(datos.getDireccionUsuario());
        if (datos.getContrasenaUsuario() != null && !datos.getContrasenaUsuario().isBlank()) {
            actual.setContrasenaUsuario(passwordEncoder.encode(datos.getContrasenaUsuario()));
        }
        return usuarioRepository.save(actual);
    }

    @Transactional
    public Usuario cambiarEstado(Long id, EstadoUsuario estado) {
        Usuario usuario = buscarPorId(id);
        usuario.setEstadoUsuario(estado);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminar(Long id) {
        usuarioRepository.delete(buscarPorId(id));
    }

    public EstadisticasUsuarioDTO estadisticas() {
        return new EstadisticasUsuarioDTO(
                usuarioRepository.count(),
                usuarioRepository.countByTipoUsuario(TipoUsuario.ADMINISTRADOR),
                usuarioRepository.countByTipoUsuario(TipoUsuario.CLIENTE),
                usuarioRepository.countByTipoUsuario(TipoUsuario.VENDEDOR));
    }

    // ---------------- validaciones (las mismas reglas del frontend)
    // ----------------

    private void normalizar(Usuario u) {
        if (u.getRunUsuario() != null)
            u.setRunUsuario(u.getRunUsuario().trim().toUpperCase());
        if (u.getNombreUsuario() != null)
            u.setNombreUsuario(u.getNombreUsuario().trim());
        if (u.getApellidosUsuario() != null)
            u.setApellidosUsuario(u.getApellidosUsuario().trim());
        if (u.getCorreoUsuario() != null)
            u.setCorreoUsuario(u.getCorreoUsuario().trim().toLowerCase());
        if (u.getRegionUsuario() != null)
            u.setRegionUsuario(u.getRegionUsuario().trim());
        if (u.getComunaUsuario() != null)
            u.setComunaUsuario(u.getComunaUsuario().trim());
        if (u.getDireccionUsuario() != null)
            u.setDireccionUsuario(u.getDireccionUsuario().trim());
    }

    private void validar(Usuario u, boolean exigirContrasena) {
        if (!esRunValido(u.getRunUsuario())) {
            throw new IllegalArgumentException(
                    "El RUN debe tener entre 7 y 9 caracteres, sin puntos ni guion, y un dígito verificador válido.");
        }
        requerido(u.getNombreUsuario(), "El nombre", 50);
        requerido(u.getApellidosUsuario(), "Los apellidos", 100);
        requerido(u.getCorreoUsuario(), "El correo", 100);
        boolean dominioValido = DOMINIOS_PERMITIDOS.stream().anyMatch(d -> u.getCorreoUsuario().endsWith(d));
        if (!dominioValido) {
            throw new IllegalArgumentException("El correo debe terminar en @duoc.cl, @profesor.duoc.cl o @gmail.com.");
        }
        if (u.getTipoUsuario() == null) {
            throw new IllegalArgumentException("Selecciona un tipo de usuario válido.");
        }
        requerido(u.getRegionUsuario(), "La región", 100);
        requerido(u.getComunaUsuario(), "La comuna", 100);
        requerido(u.getDireccionUsuario(), "La dirección", 300);

        Region region = regionRepository.findByNombreRegion(u.getRegionUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Selecciona una región válida."));
        if (!comunaRepository.existsByNombreComunaAndIdRegion(u.getComunaUsuario(), region.getId())) {
            throw new IllegalArgumentException("Selecciona una comuna válida para la región elegida.");
        }

        String clave = u.getContrasenaUsuario();
        boolean traeClave = clave != null && !clave.isBlank();
        if (exigirContrasena || traeClave) {
            if (!traeClave || clave.length() < 4 || clave.length() > 10) {
                throw new IllegalArgumentException("La contraseña debe tener entre 4 y 10 caracteres.");
            }
        }
    }

    private void requerido(String valor, String campo, int maximo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
        if (valor.length() > maximo) {
            throw new IllegalArgumentException(campo + " no puede superar los " + maximo + " caracteres.");
        }
    }

    // RUN chileno (módulo 11)
    private boolean esRunValido(String run) {
        if (run == null || run.contains(".") || run.contains("-"))
            return false;
        if (run.length() < 7 || run.length() > 9)
            return false;
        String cuerpo = run.substring(0, run.length() - 1);
        char dvIngresado = Character.toUpperCase(run.charAt(run.length() - 1));
        if (!cuerpo.matches("\\d+"))
            return false;
        return calcularDigitoVerificador(cuerpo) == dvIngresado;
    }

    private char calcularDigitoVerificador(String cuerpo) {
        int suma = 0;
        int multiplicador = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += (cuerpo.charAt(i) - '0') * multiplicador;
            multiplicador = multiplicador == 7 ? 2 : multiplicador + 1;
        }
        int resto = 11 - (suma % 11);
        if (resto == 11)
            return '0';
        if (resto == 10)
            return 'K';
        return (char) ('0' + resto);
    }
}
