package cl.duoc.sistemaMarket.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cl.duoc.sistemaMarket.config.JwtUtil;
import cl.duoc.sistemaMarket.dto.LoginRequest;
import cl.duoc.sistemaMarket.dto.LoginResponse;
import cl.duoc.sistemaMarket.exception.NoAutorizadoException;
import cl.duoc.sistemaMarket.model.EstadoUsuario;
import cl.duoc.sistemaMarket.model.Usuario;
import cl.duoc.sistemaMarket.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        if (request.getCorreoUsuario() == null || request.getCorreoUsuario().isBlank()
                || request.getContrasenaUsuario() == null || request.getContrasenaUsuario().isBlank()) {
            throw new IllegalArgumentException("Por favor completa ambos campos.");
        }

        Usuario usuario = usuarioRepository
                .findByCorreoUsuario(request.getCorreoUsuario().trim().toLowerCase())
                .orElseThrow(() -> new NoAutorizadoException("Correo o contraseña incorrectos."));

        if (!passwordEncoder.matches(request.getContrasenaUsuario(), usuario.getContrasenaUsuario())) {
            throw new NoAutorizadoException("Correo o contraseña incorrectos.");
        }
        if (usuario.getEstadoUsuario() != EstadoUsuario.ACTIVO) {
            throw new NoAutorizadoException("El usuario está inactivo.");
        }

        return new LoginResponse(jwtUtil.generarToken(usuario), usuario.getId(),
                usuario.getNombreUsuario(), usuario.getCorreoUsuario(), usuario.getTipoUsuario());
    }
}
