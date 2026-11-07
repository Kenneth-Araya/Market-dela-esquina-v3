package cl.duoc.sistemaMarket.dto;

import cl.duoc.sistemaMarket.model.Usuario;

public class UsuarioDTOMapper {
    public static Usuario toEntity(UsuarioDTO dto){
        
        if (dto == null) return null;
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(dto.getNombreUsuarioDto());
        usuario.setRutUsuario(dto.getRutUsuarioDto());
        usuario.setContactoUsuario(dto.getContactoUsuarioDto());
        usuario.setCorreoUsuario(dto.getCorreoUsuarioDto());
        usuario.setDireccionUsuario(dto.getDireccionUsuarioDto());
        usuario.setFechaUsuario(dto.getFechaUsuarioDto());
        return usuario;
    }

    public static UsuarioDTO toDto(Usuario usuario){
        
        if (usuario == null) return null;
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombreUsuarioDto(usuario.getNombreUsuario());
        dto.setRutUsuarioDto(usuario.getRutUsuario());
        dto.setContactoUsuarioDto(usuario.getContactoUsuario());
        dto.setCorreoUsuarioDto(usuario.getCorreoUsuario());
        dto.setDireccionUsuarioDto(usuario.getDireccionUsuario());
        dto.setFechaUsuarioDto(usuario.getFechaUsuario());
        return dto;
    }
}