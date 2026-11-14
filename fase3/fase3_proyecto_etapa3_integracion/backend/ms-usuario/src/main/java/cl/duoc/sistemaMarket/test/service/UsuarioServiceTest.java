package cl.duoc.sistemaMarket.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import cl.duoc.sistemaMarket.dto.EstadisticasUsuarioDTO;
import cl.duoc.sistemaMarket.model.EstadoUsuario;
import cl.duoc.sistemaMarket.model.Region;
import cl.duoc.sistemaMarket.model.TipoUsuario;
import cl.duoc.sistemaMarket.model.Usuario;
import cl.duoc.sistemaMarket.repository.ComunaRepository;
import cl.duoc.sistemaMarket.repository.RegionRepository;
import cl.duoc.sistemaMarket.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    // 12.345.678-5 es un RUN con dígito verificador válido (módulo 11).
    private static final String RUN_VALIDO = "123456785";
    // Otro RUN válido, distinto al anterior (cuerpo 10000004, DV 0).
    private static final String OTRO_RUN_VALIDO = "100000040";

    private static final String MSG_RUN = "El RUN debe tener entre 7 y 9 caracteres, sin puntos ni guion, y un dígito verificador válido.";
    private static final String MSG_CLAVE = "La contraseña debe tener entre 4 y 10 caracteres.";

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private ComunaRepository comunaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    // Datos y utilidades de prueba

    //Usuario que cumple todas las reglas de validación
    private Usuario usuarioValido() {
        Usuario u = new Usuario();
        u.setRunUsuario(RUN_VALIDO);
        u.setNombreUsuario("Juan");
        u.setApellidosUsuario("Pérez Soto");
        u.setCorreoUsuario("juan@duoc.cl");
        u.setFechaNacimientoUsuario(LocalDate.of(2000, 5, 17));
        u.setTipoUsuario(TipoUsuario.CLIENTE);
        u.setRegionUsuario("Valparaíso");
        u.setComunaUsuario("Quilpué");
        u.setDireccionUsuario("Av. Siempre Viva 742");
        u.setContrasenaUsuario("1234");
        return u;
    }

    //Usuario tal como estaría guardado en la base de datos
    private Usuario usuarioExistente() {
        Usuario u = usuarioValido();
        u.setId(5L);
        u.setContrasenaUsuario("hash-viejo");
        u.setEstadoUsuario(EstadoUsuario.ACTIVO);
        return u;
    }

    private void regionYComunaValidas() {
        when(regionRepository.findByNombreRegion("Valparaíso"))
                .thenReturn(Optional.of(new Region(1L, "Valparaíso")));
        when(comunaRepository.existsByNombreComunaAndIdRegion("Quilpué", 1L)).thenReturn(true);
    }

    private void guardarDevuelveLoMismo() {
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private void exigirArgumentoInvalido(Usuario usuario, String mensaje) {
        assertThatThrownBy(() -> usuarioService.registrar(usuario))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(mensaje);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // Consultas simples
    @Nested
    @DisplayName("listar / buscarPorId / estadisticas")
    class Consultas {

        @Test
        @DisplayName("listar devuelve todos los usuarios")
        void listar() {
            List<Usuario> usuarios = List.of(usuarioExistente(), usuarioValido());
            when(usuarioRepository.findAll()).thenReturn(usuarios);

            assertThat(usuarioService.listar()).isEqualTo(usuarios);
        }

        @Test
        @DisplayName("buscarPorId devuelve el usuario si existe")
        void buscarPorIdExistente() {
            Usuario usuario = usuarioExistente();
            when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuario));

            assertThat(usuarioService.buscarPorId(5L)).isSameAs(usuario);
        }

        @Test
        @DisplayName("buscarPorId lanza NoSuchElementException si no existe")
        void buscarPorIdInexistente() {
            when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.buscarPorId(99L))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Usuario no encontrado: 99");
        }

        @Test
        @DisplayName("estadisticas cuenta total y por tipo de usuario")
        void estadisticas() {
            when(usuarioRepository.count()).thenReturn(10L);
            when(usuarioRepository.countByTipoUsuario(TipoUsuario.ADMINISTRADOR)).thenReturn(2L);
            when(usuarioRepository.countByTipoUsuario(TipoUsuario.CLIENTE)).thenReturn(6L);
            when(usuarioRepository.countByTipoUsuario(TipoUsuario.VENDEDOR)).thenReturn(2L);

            EstadisticasUsuarioDTO dto = usuarioService.estadisticas();

            assertThat(dto).isEqualTo(new EstadisticasUsuarioDTO(10L, 2L, 6L, 2L));
        }
    }

    // registrar
    @Nested
    @DisplayName("registrar")
    class Registrar {

        @Test
        @DisplayName("registro correcto: fuerza CLIENTE/ACTIVO, normaliza datos y cifra la contraseña")
        void registroCorrecto() {
            regionYComunaValidas();
            guardarDevuelveLoMismo();
            when(passwordEncoder.encode("1234")).thenReturn("hash-1234");

            Usuario entrada = usuarioValido();
            entrada.setId(99L); // el id debe ignorarse
            entrada.setTipoUsuario(TipoUsuario.ADMINISTRADOR); // no puede registrarse como admin
            entrada.setRunUsuario("  10000013k "); // espacios y 'k' minúscula
            entrada.setCorreoUsuario("  JUAN@Duoc.CL ");
            entrada.setNombreUsuario("  Juan ");

            Usuario resultado = usuarioService.registrar(entrada);

            assertThat(resultado.getTipoUsuario()).isEqualTo(TipoUsuario.CLIENTE);
            assertThat(resultado.getEstadoUsuario()).isEqualTo(EstadoUsuario.ACTIVO);
            assertThat(resultado.getId()).isNull();
            assertThat(resultado.getFechaRegistroUsuario()).isNotNull();
            assertThat(resultado.getContrasenaUsuario()).isEqualTo("hash-1234");
            assertThat(resultado.getRunUsuario()).isEqualTo("10000013K");
            assertThat(resultado.getCorreoUsuario()).isEqualTo("juan@duoc.cl");
            assertThat(resultado.getNombreUsuario()).isEqualTo("Juan");
            verify(passwordEncoder).encode("1234");
            verify(usuarioRepository).save(any(Usuario.class));
        }

        @ParameterizedTest(name = "RUN válido: {0}")
        @ValueSource(strings = { "123456785", "100000040", "10000013K", "10000013k", "12345674", "1234560", "100008K" })
        @DisplayName("acepta RUN válidos (dígito 0, K y largos de 7 a 9 caracteres)")
        void aceptaRunValidos(String run) {
            regionYComunaValidas();
            guardarDevuelveLoMismo();
            when(passwordEncoder.encode("1234")).thenReturn("hash");
            Usuario usuario = usuarioValido();
            usuario.setRunUsuario(run);

            Usuario resultado = usuarioService.registrar(usuario);

            assertThat(resultado.getRunUsuario()).isEqualTo(run.toUpperCase());
        }

        @ParameterizedTest(name = "dominio válido: {0}")
        @ValueSource(strings = { "ana@duoc.cl", "ana@profesor.duoc.cl", "ana@gmail.com" })
        @DisplayName("acepta los dominios de correo permitidos")
        void aceptaDominiosPermitidos(String correo) {
            regionYComunaValidas();
            guardarDevuelveLoMismo();
            when(passwordEncoder.encode("1234")).thenReturn("hash");
            Usuario usuario = usuarioValido();
            usuario.setCorreoUsuario(correo);

            assertThat(usuarioService.registrar(usuario).getCorreoUsuario()).isEqualTo(correo);
        }

        @ParameterizedTest(name = "contraseña de {0} caracteres")
        @ValueSource(strings = { "1234", "1234567890" })
        @DisplayName("acepta contraseñas en los límites (4 y 10 caracteres)")
        void aceptaContrasenasEnElLimite(String clave) {
            regionYComunaValidas();
            guardarDevuelveLoMismo();
            when(passwordEncoder.encode(clave)).thenReturn("hash");
            Usuario usuario = usuarioValido();
            usuario.setContrasenaUsuario(clave);

            assertThat(usuarioService.registrar(usuario).getContrasenaUsuario()).isEqualTo("hash");
        }

        @ParameterizedTest(name = "RUN inválido: [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = { "   ", "12.345.678-5", "12345678-5", "123456", "1234567890", "123456780", "1234567K5" })
        @DisplayName("rechaza RUN nulo, vacío, con formato incorrecto o dígito verificador inválido")
        void rechazaRunInvalido(String run) {
            Usuario usuario = usuarioValido();
            usuario.setRunUsuario(run);

            exigirArgumentoInvalido(usuario, MSG_RUN);
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("cl.duoc.sistemaMarket.service.UsuarioServiceTest#camposInvalidos")
        @DisplayName("rechaza campos obligatorios vacíos, demasiado largos o con dominio no permitido")
        void rechazaCamposInvalidos(String descripcion, Consumer<Usuario> cambio, String mensajeEsperado) {
            Usuario usuario = usuarioValido();
            cambio.accept(usuario);

            exigirArgumentoInvalido(usuario, mensajeEsperado);
        }

        @Test
        @DisplayName("rechaza una región que no existe")
        void rechazaRegionInexistente() {
            when(regionRepository.findByNombreRegion("Valparaíso")).thenReturn(Optional.empty());

            exigirArgumentoInvalido(usuarioValido(), "Selecciona una región válida.");
        }

        @Test
        @DisplayName("rechaza una comuna que no pertenece a la región elegida")
        void rechazaComunaDeOtraRegion() {
            when(regionRepository.findByNombreRegion("Valparaíso"))
                    .thenReturn(Optional.of(new Region(1L, "Valparaíso")));
            when(comunaRepository.existsByNombreComunaAndIdRegion("Quilpué", 1L)).thenReturn(false);

            exigirArgumentoInvalido(usuarioValido(), "Selecciona una comuna válida para la región elegida.");
        }

        @ParameterizedTest(name = "contraseña inválida: [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = { "   ", "123", "12345678901" })
        @DisplayName("rechaza contraseñas nulas, vacías, muy cortas o muy largas")
        void rechazaContrasenaInvalida(String clave) {
            regionYComunaValidas();
            Usuario usuario = usuarioValido();
            usuario.setContrasenaUsuario(clave);

            exigirArgumentoInvalido(usuario, MSG_CLAVE);
        }

        @Test
        @DisplayName("rechaza un RUN que ya existe")
        void rechazaRunDuplicado() {
            regionYComunaValidas();
            when(usuarioRepository.existsByRunUsuario(RUN_VALIDO)).thenReturn(true);

            assertThatThrownBy(() -> usuarioService.registrar(usuarioValido()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Ya existe un usuario con ese RUN.");
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }

        @Test
        @DisplayName("rechaza un correo que ya existe")
        void rechazaCorreoDuplicado() {
            regionYComunaValidas();
            when(usuarioRepository.existsByCorreoUsuario("juan@duoc.cl")).thenReturn(true);

            assertThatThrownBy(() -> usuarioService.registrar(usuarioValido()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Ya existe un usuario con ese correo.");
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }
    }

    // actualizar
    @Nested
    @DisplayName("actualizar")
    class Actualizar {

        @Test
        @DisplayName("actualiza los datos y conserva la contraseña si no se envía una nueva")
        void actualizaSinCambiarContrasena() {
            Usuario actual = usuarioExistente();
            when(usuarioRepository.findById(5L)).thenReturn(Optional.of(actual));
            regionYComunaValidas();
            guardarDevuelveLoMismo();

            Usuario datos = usuarioValido();
            datos.setNombreUsuario("  Juan Pablo ");
            datos.setTipoUsuario(TipoUsuario.VENDEDOR);
            datos.setContrasenaUsuario(null);

            Usuario resultado = usuarioService.actualizar(5L, datos);

            assertThat(resultado.getId()).isEqualTo(5L);
            assertThat(resultado.getNombreUsuario()).isEqualTo("Juan Pablo");
            assertThat(resultado.getTipoUsuario()).isEqualTo(TipoUsuario.VENDEDOR);
            assertThat(resultado.getEstadoUsuario()).isEqualTo(EstadoUsuario.ACTIVO);
            assertThat(resultado.getContrasenaUsuario()).isEqualTo("hash-viejo");
            // Como RUN y correo no cambiaron, no hace falta consultar duplicados.
            verify(usuarioRepository, never()).existsByRunUsuario(anyString());
            verify(usuarioRepository, never()).existsByCorreoUsuario(anyString());
            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        @DisplayName("una contraseña en blanco se ignora y se mantiene la anterior")
        void contrasenaEnBlancoSeIgnora() {
            Usuario actual = usuarioExistente();
            when(usuarioRepository.findById(5L)).thenReturn(Optional.of(actual));
            regionYComunaValidas();
            guardarDevuelveLoMismo();

            Usuario datos = usuarioValido();
            datos.setContrasenaUsuario("   ");

            Usuario resultado = usuarioService.actualizar(5L, datos);

            assertThat(resultado.getContrasenaUsuario()).isEqualTo("hash-viejo");
            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        @DisplayName("una contraseña nueva válida se cifra y reemplaza la anterior")
        void cambiaContrasena() {
            Usuario actual = usuarioExistente();
            when(usuarioRepository.findById(5L)).thenReturn(Optional.of(actual));
            regionYComunaValidas();
            guardarDevuelveLoMismo();
            when(passwordEncoder.encode("nueva1")).thenReturn("hash-nuevo");

            Usuario datos = usuarioValido();
            datos.setContrasenaUsuario("nueva1");

            Usuario resultado = usuarioService.actualizar(5L, datos);

            assertThat(resultado.getContrasenaUsuario()).isEqualTo("hash-nuevo");
        }

        @ParameterizedTest(name = "contraseña nueva inválida: [{0}]")
        @ValueSource(strings = { "123", "12345678901" })
        @DisplayName("rechaza una contraseña nueva fuera del largo permitido")
        void rechazaContrasenaNuevaInvalida(String clave) {
            when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuarioExistente()));
            regionYComunaValidas();
            Usuario datos = usuarioValido();
            datos.setContrasenaUsuario(clave);

            assertThatThrownBy(() -> usuarioService.actualizar(5L, datos))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(MSG_CLAVE);
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }

        @Test
        @DisplayName("permite cambiar el RUN a uno que no está en uso")
        void cambiaRunLibre() {
            Usuario actual = usuarioExistente();
            when(usuarioRepository.findById(5L)).thenReturn(Optional.of(actual));
            regionYComunaValidas();
            guardarDevuelveLoMismo();
            when(usuarioRepository.existsByRunUsuario(OTRO_RUN_VALIDO)).thenReturn(false);

            Usuario datos = usuarioValido();
            datos.setRunUsuario(OTRO_RUN_VALIDO);

            Usuario resultado = usuarioService.actualizar(5L, datos);

            assertThat(resultado.getRunUsuario()).isEqualTo(OTRO_RUN_VALIDO);
        }

        @Test
        @DisplayName("rechaza cambiar el RUN a uno que ya usa otro usuario")
        void rechazaRunEnUso() {
            when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuarioExistente()));
            regionYComunaValidas();
            when(usuarioRepository.existsByRunUsuario(OTRO_RUN_VALIDO)).thenReturn(true);

            Usuario datos = usuarioValido();
            datos.setRunUsuario(OTRO_RUN_VALIDO);

            assertThatThrownBy(() -> usuarioService.actualizar(5L, datos))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Ya existe un usuario con ese RUN.");
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }

        @Test
        @DisplayName("permite cambiar el correo a uno que no está en uso")
        void cambiaCorreoLibre() {
            Usuario actual = usuarioExistente();
            when(usuarioRepository.findById(5L)).thenReturn(Optional.of(actual));
            regionYComunaValidas();
            guardarDevuelveLoMismo();
            when(usuarioRepository.existsByCorreoUsuario("nuevo@duoc.cl")).thenReturn(false);

            Usuario datos = usuarioValido();
            datos.setCorreoUsuario("  NUEVO@duoc.cl ");

            Usuario resultado = usuarioService.actualizar(5L, datos);

            assertThat(resultado.getCorreoUsuario()).isEqualTo("nuevo@duoc.cl");
        }

        @Test
        @DisplayName("rechaza cambiar el correo a uno que ya usa otro usuario")
        void rechazaCorreoEnUso() {
            when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuarioExistente()));
            regionYComunaValidas();
            when(usuarioRepository.existsByCorreoUsuario("nuevo@duoc.cl")).thenReturn(true);

            Usuario datos = usuarioValido();
            datos.setCorreoUsuario("nuevo@duoc.cl");

            assertThatThrownBy(() -> usuarioService.actualizar(5L, datos))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Ya existe un usuario con ese correo.");
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }

        @Test
        @DisplayName("rechaza datos sin tipo de usuario")
        void rechazaSinTipoUsuario() {
            when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuarioExistente()));
            Usuario datos = usuarioValido();
            datos.setTipoUsuario(null);

            assertThatThrownBy(() -> usuarioService.actualizar(5L, datos))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Selecciona un tipo de usuario válido.");
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }

        @Test
        @DisplayName("rechaza datos con RUN inválido")
        void rechazaRunInvalido() {
            when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuarioExistente()));
            Usuario datos = usuarioValido();
            datos.setRunUsuario("123456780");

            assertThatThrownBy(() -> usuarioService.actualizar(5L, datos))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(MSG_RUN);
        }

        @Test
        @DisplayName("lanza NoSuchElementException si el usuario no existe")
        void usuarioInexistente() {
            when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.actualizar(99L, usuarioValido()))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Usuario no encontrado: 99");
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }
    }

    // cambiarEstado / eliminar
    @Nested
    @DisplayName("cambiarEstado y eliminar")
    class EstadoYEliminacion {

        @Test
        @DisplayName("cambiarEstado actualiza y guarda el estado")
        void cambiarEstado() {
            Usuario usuario = usuarioExistente();
            when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuario));
            guardarDevuelveLoMismo();

            Usuario resultado = usuarioService.cambiarEstado(5L, EstadoUsuario.INACTIVO);

            assertThat(resultado.getEstadoUsuario()).isEqualTo(EstadoUsuario.INACTIVO);
            verify(usuarioRepository).save(usuario);
        }

        @Test
        @DisplayName("cambiarEstado lanza NoSuchElementException si el usuario no existe")
        void cambiarEstadoInexistente() {
            when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.cambiarEstado(99L, EstadoUsuario.INACTIVO))
                    .isInstanceOf(NoSuchElementException.class);
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }

        @Test
        @DisplayName("eliminar borra el usuario encontrado")
        void eliminar() {
            Usuario usuario = usuarioExistente();
            when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuario));

            usuarioService.eliminar(5L);

            verify(usuarioRepository).delete(usuario);
        }

        @Test
        @DisplayName("eliminar lanza NoSuchElementException si el usuario no existe")
        void eliminarInexistente() {
            when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> usuarioService.eliminar(99L))
                    .isInstanceOf(NoSuchElementException.class);
            verify(usuarioRepository, never()).delete(any(Usuario.class));
        }
    }

    // Tabla de casos inválidos (los usa @Nested Registrar)
    static Stream<Arguments> camposInvalidos() {
        return Stream.of(
                caso("nombre nulo", u -> u.setNombreUsuario(null), "El nombre es obligatorio."),
                caso("nombre en blanco", u -> u.setNombreUsuario("   "), "El nombre es obligatorio."),
                caso("nombre de 51 caracteres", u -> u.setNombreUsuario("a".repeat(51)),
                        "El nombre no puede superar los 50 caracteres."),

                caso("apellidos nulos", u -> u.setApellidosUsuario(null), "Los apellidos es obligatorio."),
                caso("apellidos en blanco", u -> u.setApellidosUsuario(" "), "Los apellidos es obligatorio."),
                caso("apellidos de 101 caracteres", u -> u.setApellidosUsuario("a".repeat(101)),
                        "Los apellidos no puede superar los 100 caracteres."),

                caso("correo nulo", u -> u.setCorreoUsuario(null), "El correo es obligatorio."),
                caso("correo en blanco", u -> u.setCorreoUsuario("  "), "El correo es obligatorio."),
                caso("correo de más de 100 caracteres", u -> u.setCorreoUsuario("a".repeat(95) + "@duoc.cl"),
                        "El correo no puede superar los 100 caracteres."),
                caso("correo con dominio no permitido", u -> u.setCorreoUsuario("juan@hotmail.com"),
                        "El correo debe terminar en @duoc.cl, @profesor.duoc.cl o @gmail.com."),

                caso("región nula", u -> u.setRegionUsuario(null), "La región es obligatorio."),
                caso("región en blanco", u -> u.setRegionUsuario(" "), "La región es obligatorio."),
                caso("región de 101 caracteres", u -> u.setRegionUsuario("a".repeat(101)),
                        "La región no puede superar los 100 caracteres."),

                caso("comuna nula", u -> u.setComunaUsuario(null), "La comuna es obligatorio."),
                caso("comuna en blanco", u -> u.setComunaUsuario(" "), "La comuna es obligatorio."),
                caso("comuna de 101 caracteres", u -> u.setComunaUsuario("a".repeat(101)),
                        "La comuna no puede superar los 100 caracteres."),

                caso("dirección nula", u -> u.setDireccionUsuario(null), "La dirección es obligatorio."),
                caso("dirección en blanco", u -> u.setDireccionUsuario(" "), "La dirección es obligatorio."),
                caso("dirección de 301 caracteres", u -> u.setDireccionUsuario("a".repeat(301)),
                        "La dirección no puede superar los 300 caracteres."));
    }

    private static Arguments caso(String descripcion, Consumer<Usuario> cambio, String mensajeEsperado) {
        return Arguments.of(descripcion, cambio, mensajeEsperado);
    }
}