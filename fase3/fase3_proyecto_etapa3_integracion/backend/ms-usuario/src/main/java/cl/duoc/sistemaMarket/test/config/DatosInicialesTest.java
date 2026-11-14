package cl.duoc.sistemaMarket.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import cl.duoc.sistemaMarket.model.Comuna;
import cl.duoc.sistemaMarket.model.EstadoUsuario;
import cl.duoc.sistemaMarket.model.Region;
import cl.duoc.sistemaMarket.model.TipoUsuario;
import cl.duoc.sistemaMarket.model.Usuario;
import cl.duoc.sistemaMarket.repository.ComunaRepository;
import cl.duoc.sistemaMarket.repository.RegionRepository;
import cl.duoc.sistemaMarket.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class DatosInicialesTest {

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private ComunaRepository comunaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DatosIniciales datosIniciales;

    @Test
    @DisplayName("con la base vacía crea las regiones, sus comunas y el administrador inicial")
    void baseVacia() {
        when(regionRepository.count()).thenReturn(0L);
        when(regionRepository.save(any(Region.class))).thenAnswer(inv -> {
            Region region = inv.getArgument(0);
            region.setId(10L);
            return region;
        });
        when(usuarioRepository.existsByCorreoUsuario("admin@duoc.cl")).thenReturn(false);
        when(passwordEncoder.encode("admin123")).thenReturn("hash-admin");

        datosIniciales.run();

        // 2 regiones y 4 + 3 comunas
        ArgumentCaptor<Region> regiones = ArgumentCaptor.forClass(Region.class);
        verify(regionRepository, times(2)).save(regiones.capture());
        assertThat(regiones.getAllValues()).extracting(Region::getNombreRegion)
                .containsExactly("Región Metropolitana", "Valparaíso");

        ArgumentCaptor<Comuna> comunas = ArgumentCaptor.forClass(Comuna.class);
        verify(comunaRepository, times(7)).save(comunas.capture());
        assertThat(comunas.getAllValues()).extracting(Comuna::getNombreComuna)
                .containsExactly("Santiago", "Maipú", "Puente Alto", "Las Condes",
                        "Valparaíso", "Viña del Mar", "Quilpué");
        assertThat(comunas.getAllValues()).extracting(Comuna::getIdRegion).containsOnly(10L);

        ArgumentCaptor<Usuario> admin = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(admin.capture());
        Usuario guardado = admin.getValue();
        assertThat(guardado.getCorreoUsuario()).isEqualTo("admin@duoc.cl");
        assertThat(guardado.getTipoUsuario()).isEqualTo(TipoUsuario.ADMINISTRADOR);
        assertThat(guardado.getEstadoUsuario()).isEqualTo(EstadoUsuario.ACTIVO);
        assertThat(guardado.getContrasenaUsuario()).isEqualTo("hash-admin");
        assertThat(guardado.getFechaRegistroUsuario()).isNotNull();
    }

    @Test
    @DisplayName("si ya existen regiones y el administrador no crea nada")
    void baseConDatos() {
        when(regionRepository.count()).thenReturn(3L);
        when(usuarioRepository.existsByCorreoUsuario("admin@duoc.cl")).thenReturn(true);

        datosIniciales.run();

        verify(regionRepository, never()).save(any(Region.class));
        verify(comunaRepository, never()).save(any(Comuna.class));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("si hay regiones pero falta el administrador solo crea el administrador")
    void soloFaltaAdministrador() {
        when(regionRepository.count()).thenReturn(2L);
        when(usuarioRepository.existsByCorreoUsuario("admin@duoc.cl")).thenReturn(false);
        when(passwordEncoder.encode("admin123")).thenReturn("hash-admin");

        datosIniciales.run();

        verify(regionRepository, never()).save(any(Region.class));
        verify(usuarioRepository).save(any(Usuario.class));
    }
}
