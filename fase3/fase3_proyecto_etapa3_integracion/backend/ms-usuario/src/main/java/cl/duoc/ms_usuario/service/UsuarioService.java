package cl.duoc.sistemaMarket.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cl.duoc.sistemaMarket.dto.UsuarioDTO;
import cl.duoc.sistemaMarket.dto.UsuarioDTOMapper;
import cl.duoc.sistemaMarket.exeptions.RecursoNoEncontradoException;
import cl.duoc.sistemaMarket.model.Usuario;
import cl.duoc.sistemaMarket.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Listar usuarios
    public List<UsuarioDTO> listarTodos() {
        log.info("Iniciando consulta para listar todos los usuarios");

        List<Usuario> usuarios = usuarioRepository.findAll();
        List<UsuarioDTO> usuarioDTOs = new ArrayList<>();

        if (!usuarios.isEmpty()) {
            for (Usuario usuario : usuarios) {
                usuarioDTOs.add(UsuarioDTOMapper.toDto(usuario));
            }
            log.info("Se encontraron {} usuario(s)", usuarios.size());
        } else {
            log.warn("No se encontraron usuarios en el sistema");
        }

        return usuarioDTOs;
    }

    // Guardar usuarios
    public UsuarioDTO guardarUsuario(UsuarioDTO usuarioDTO) {
        log.info("Iniciando proceso de guardado de usuario");

        // Regla: nombre no nulo ni vacío
        if (usuarioDTO == null || usuarioDTO.getNombreUsuarioDto() == null || usuarioDTO.getNombreUsuarioDto().isBlank()) {
            log.error("Validación fallida: el usuario o su nombre son nulos o vacíos");
            throw new IllegalArgumentException("El usuario o su nombre no pueden ser nulos o vacíos");
        }

        // Regla: RUT debe contener guion
        String rut = usuarioDTO.getRutUsuarioDto();
        if (rut == null || !rut.contains("-")) {
            log.error("Validación fallida: el RUT '{}' no contiene guion separador", rut);
            throw new IllegalArgumentException("El RUT debe contener guion separador (ej: 12345678-9). Valor recibido: " + rut);
        }

        // Regla: correo debe contener '@'
        String correo = usuarioDTO.getCorreoUsuarioDto();
        if (correo == null || !correo.contains("@")) {
            log.error("Validación fallida: el correo '{}' no contiene '@'", correo);
            throw new IllegalArgumentException("El correo debe contener '@' (ej: usuario@correo.com). Valor recibido: " + correo);
        }

        Usuario usuario = UsuarioDTOMapper.toEntity(usuarioDTO);
        Usuario guardado = usuarioRepository.save(usuario);

        if (guardado != null) {
            log.info("Usuario con rut {} guardado exitosamente", usuarioDTO.getRutUsuarioDto());
        } else {
            log.warn("No se pudo guardar usuario con rut {}", usuarioDTO.getRutUsuarioDto());
        }

        return UsuarioDTOMapper.toDto(guardado);
    }

    // Eliminar usuarios
    public void eliminarPorId(int id) {
        log.info("Eliminando usuario con id: {}", id);
        usuarioRepository.deleteById(id);
        log.info("Usuario con id {} eliminado exitosamente", id);
    }

    // Buscar usuario por id
    public UsuarioDTO findById(int id) {
        log.info("Buscando usuario con id: {}", id);

        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario == null) {
            log.error("Usuario no encontrado con id: {}", id);
            throw new RecursoNoEncontradoException("No existe usuario con id: " + id);
        }

        log.info("Usuario con id {} encontrado exitosamente", id);
        return UsuarioDTOMapper.toDto(usuario);
    }

    // Actualizar usuarios
    public UsuarioDTO actualizarUsuario(int id, UsuarioDTO dto) {
        log.info("Iniciando actualización de usuario con id: {}", id);

        // Regla: RUT debe contener guion
        String rut = dto.getRutUsuarioDto();
        if (rut == null || !rut.contains("-")) {
            log.error("Validación fallida: el RUT '{}' no contiene guion separador", rut);
            throw new IllegalArgumentException("El RUT debe contener guion separador (ej: 12345678-9). Valor recibido: " + rut);
        }

        // Regla: correo debe contener '@'
        String correo = dto.getCorreoUsuarioDto();
        if (correo == null || !correo.contains("@")) {
            log.error("Validación fallida: el correo '{}' no contiene '@'", correo);
            throw new IllegalArgumentException("El correo debe contener '@' (ej: usuario@correo.com). Valor recibido: " + correo);
        }

        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> {
            log.error("Usuario no encontrado con id: {}", id);
            return new RuntimeException("Usuario no encontrado con id: " + id);
        });

        usuario.setNombreUsuario(dto.getNombreUsuarioDto());
        usuario.setRutUsuario(dto.getRutUsuarioDto());
        usuario.setContactoUsuario(dto.getContactoUsuarioDto());
        usuario.setCorreoUsuario(dto.getCorreoUsuarioDto());

        Usuario actualizado = usuarioRepository.save(usuario);
        log.info("Usuario con id {} actualizado exitosamente", id);

        return UsuarioDTOMapper.toDto(actualizado);
    }
}