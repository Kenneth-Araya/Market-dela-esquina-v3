package cl.duoc.sistemaMarket.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cl.duoc.sistemaMarket.dto.EstadisticasUsuarioDTO;
import cl.duoc.sistemaMarket.exception.GlobalExceptionHandler;
import cl.duoc.sistemaMarket.model.EstadoUsuario;
import cl.duoc.sistemaMarket.model.TipoUsuario;
import cl.duoc.sistemaMarket.model.Usuario;
import cl.duoc.sistemaMarket.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private static final String JSON_USUARIO = """
            {
              "runUsuario": "123456785",
              "nombreUsuario": "Juan",
              "apellidosUsuario": "Pérez",
              "correoUsuario": "juan@duoc.cl",
              "regionUsuario": "Valparaíso",
              "comunaUsuario": "Quilpué",
              "direccionUsuario": "Calle 1",
              "contrasenaUsuario": "1234"
            }
            """;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private Usuario usuario(Long id, String correo) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setRunUsuario("123456785");
        u.setNombreUsuario("Juan");
        u.setApellidosUsuario("Pérez");
        u.setCorreoUsuario(correo);
        u.setTipoUsuario(TipoUsuario.CLIENTE);
        u.setEstadoUsuario(EstadoUsuario.ACTIVO);
        u.setContrasenaUsuario("hash-secreto");
        return u;
    }

    @Test
    @DisplayName("POST /api/usuarios -> 201 y no devuelve la contraseña")
    void registrar() throws Exception {
        when(usuarioService.registrar(any(Usuario.class))).thenReturn(usuario(1L, "juan@duoc.cl"));

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_USUARIO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.correoUsuario").value("juan@duoc.cl"))
                .andExpect(jsonPath("$.contrasenaUsuario").doesNotExist());

        // La contraseña sí se acepta en la entrada (WRITE_ONLY).
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioService).registrar(captor.capture());
        assertThat(captor.getValue().getContrasenaUsuario()).isEqualTo("1234");
        assertThat(captor.getValue().getRunUsuario()).isEqualTo("123456785");
    }

    @Test
    @DisplayName("POST /api/usuarios con datos inválidos -> 400 con mensaje")
    void registrarConDatosInvalidos() throws Exception {
        when(usuarioService.registrar(any(Usuario.class)))
                .thenThrow(new IllegalArgumentException("El nombre es obligatorio."));

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_USUARIO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El nombre es obligatorio."));
    }

    @Test
    @DisplayName("POST /api/usuarios con RUN o correo repetido -> 409")
    void registrarDuplicado() throws Exception {
        when(usuarioService.registrar(any(Usuario.class)))
                .thenThrow(new IllegalStateException("Ya existe un usuario con ese RUN."));

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_USUARIO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("Ya existe un usuario con ese RUN."));
    }

    @Test
    @DisplayName("POST /api/usuarios con JSON mal formado -> 400 sin llamar al servicio")
    void registrarConJsonMalFormado() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{esto no es json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El cuerpo de la solicitud es inválido."));

        verifyNoInteractions(usuarioService);
    }

    @Test
    @DisplayName("GET /api/usuarios -> lista de usuarios")
    void listar() throws Exception {
        when(usuarioService.listar()).thenReturn(List.of(usuario(1L, "a@duoc.cl"), usuario(2L, "b@duoc.cl")));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].correoUsuario").value("a@duoc.cl"))
                .andExpect(jsonPath("$[1].correoUsuario").value("b@duoc.cl"))
                .andExpect(jsonPath("$[0].contrasenaUsuario").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/usuarios/estadisticas -> totales por tipo")
    void estadisticas() throws Exception {
        when(usuarioService.estadisticas()).thenReturn(new EstadisticasUsuarioDTO(10, 2, 6, 2));

        mockMvc.perform(get("/api/usuarios/estadisticas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsuarios").value(10))
                .andExpect(jsonPath("$.administradores").value(2))
                .andExpect(jsonPath("$.clientes").value(6))
                .andExpect(jsonPath("$.vendedores").value(2));
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} -> 200 con el usuario")
    void buscarPorId() throws Exception {
        when(usuarioService.buscarPorId(5L)).thenReturn(usuario(5L, "juan@duoc.cl"));

        mockMvc.perform(get("/api/usuarios/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.tipoUsuario").value("CLIENTE"));
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} inexistente -> 404")
    void buscarPorIdInexistente() throws Exception {
        when(usuarioService.buscarPorId(99L)).thenThrow(new NoSuchElementException("Usuario no encontrado: 99"));

        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Usuario no encontrado: 99"));
    }

    @Test
    @DisplayName("PUT /api/usuarios/{id} -> 200 con el usuario actualizado")
    void actualizar() throws Exception {
        when(usuarioService.actualizar(eq(5L), any(Usuario.class)))
                .thenReturn(usuario(5L, "juan@duoc.cl"));

        mockMvc.perform(put("/api/usuarios/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_USUARIO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioService).actualizar(eq(5L), captor.capture());
        assertThat(captor.getValue().getCorreoUsuario()).isEqualTo("juan@duoc.cl");
    }

    @Test
    @DisplayName("PATCH /api/usuarios/{id}/estado -> 200 con el nuevo estado")
    void cambiarEstado() throws Exception {
        Usuario inactivo = usuario(5L, "juan@duoc.cl");
        inactivo.setEstadoUsuario(EstadoUsuario.INACTIVO);
        when(usuarioService.cambiarEstado(5L, EstadoUsuario.INACTIVO)).thenReturn(inactivo);

        mockMvc.perform(patch("/api/usuarios/5/estado").param("estado", "INACTIVO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoUsuario").value("INACTIVO"));
    }

    @Test
    @DisplayName("PATCH /api/usuarios/{id}/estado con un estado desconocido -> 400")
    void cambiarEstadoInvalido() throws Exception {
        mockMvc.perform(patch("/api/usuarios/5/estado").param("estado", "SUSPENDIDO"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(usuarioService);
    }

    @Test
    @DisplayName("DELETE /api/usuarios/{id} -> 204")
    void eliminar() throws Exception {
        mockMvc.perform(delete("/api/usuarios/5")).andExpect(status().isNoContent());

        verify(usuarioService).eliminar(5L);
    }

    @Test
    @DisplayName("DELETE /api/usuarios/{id} inexistente -> 404")
    void eliminarInexistente() throws Exception {
        doThrow(new NoSuchElementException("Usuario no encontrado: 99"))
                .when(usuarioService).eliminar(99L);

        mockMvc.perform(delete("/api/usuarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Usuario no encontrado: 99"));
    }
}
