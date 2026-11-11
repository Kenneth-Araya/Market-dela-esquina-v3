package cl.duoc.sistemaMarket.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.sistemaMarket.model.EstadoContacto;
import cl.duoc.sistemaMarket.model.MensajeContacto;
import cl.duoc.sistemaMarket.repository.MensajeContactoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactoService {

    private static final Pattern CORREO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final MensajeContactoRepository mensajeRepository;

    @Transactional
    public MensajeContacto enviar(MensajeContacto mensaje) {
        if (mensaje.getNombreContacto() == null || mensaje.getNombreContacto().isBlank()) {
            throw new IllegalArgumentException("Por favor, ingresa tu nombre completo.");
        }
        if (mensaje.getCorreoContacto() == null || !CORREO.matcher(mensaje.getCorreoContacto().trim()).matches()) {
            throw new IllegalArgumentException("Por favor, ingrese un correo electrónico válido.");
        }
        if (mensaje.getContenidoContacto() == null || mensaje.getContenidoContacto().isBlank()) {
            throw new IllegalArgumentException("El contenido del mensaje no puede estar vacío.");
        }
        if (mensaje.getNombreContacto().length() > 100 || mensaje.getCorreoContacto().length() > 100) {
            throw new IllegalArgumentException("El nombre y el correo no pueden superar los 100 caracteres.");
        }
        if (mensaje.getContenidoContacto().length() > 2000) {
            throw new IllegalArgumentException("El mensaje no puede superar los 2000 caracteres.");
        }
        mensaje.setId(null);
        mensaje.setNombreContacto(mensaje.getNombreContacto().trim());
        mensaje.setCorreoContacto(mensaje.getCorreoContacto().trim());
        mensaje.setContenidoContacto(mensaje.getContenidoContacto().trim());
        mensaje.setFechaEnvioContacto(LocalDateTime.now());
        mensaje.setEstadoContacto(EstadoContacto.NUEVO);
        return mensajeRepository.save(mensaje);
    }

    public List<MensajeContacto> listar() {
        return mensajeRepository.findAllByOrderByFechaEnvioContactoDesc();
    }

    public MensajeContacto buscarPorId(Long id) {
        return mensajeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Mensaje no encontrado: " + id));
    }

    @Transactional
    public MensajeContacto cambiarEstado(Long id, EstadoContacto estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }
        MensajeContacto mensaje = buscarPorId(id);
        mensaje.setEstadoContacto(estado);
        return mensajeRepository.save(mensaje);
    }

    @Transactional
    public void eliminar(Long id) {
        mensajeRepository.delete(buscarPorId(id));
    }
}
