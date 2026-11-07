package cl.duoc.sistemaMarket.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cl.duoc.sistemaMarket.dto.UsuarioDTO;
import cl.duoc.sistemaMarket.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(
        summary = "Listar todos los usuarios",
        description = "obtiene una lista con todos los usuarios"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de usuarios obtenido con exito"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    //Listar usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos(){
        List<UsuarioDTO> usuarioDTOs = usuarioService.listarTodos();
        return ResponseEntity
        .ok(usuarioDTOs);
    }

     @Operation(
        summary = "registrar usuarios",
        description = "Crea y guarda una nuevo usuario en el sistema"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Usuario creado con exito"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "datos del usuario ingresados no son validos"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error de servidor"
        )
    })
    //Crear usuarios
    @PostMapping
    public ResponseEntity<UsuarioDTO> guardarUsuario(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "datos del usuario a registrar",
            required = true
    )
    @Valid @RequestBody UsuarioDTO usuarioDTO){
        UsuarioDTO creado = usuarioService.guardarUsuario(usuarioDTO);
        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(creado);
    }

     @Operation(
        summary = "eliminar usuario",
        description = "Elimina un usuario existente mediante su id"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode="204",
            description = "Usuario eliminado con exito"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error en el servidor "
        )
    }) 
    //Eliminar usuarios
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(
        @Parameter(
            description = "id del usuario a eliminar",
            example = "1",
            required = true
    )
    @PathVariable int id){
        usuarioService.eliminarPorId(id);
        return ResponseEntity
        .noContent()
        .build();
    }

    @Operation(
        summary = "Actualizar Usuario",
        description = "Actualiza la información de un usuario"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode="200",
            description="Usuario actualizado con exito"
        ),
        @ApiResponse(
            responseCode ="404",
            description = "Usuario no encontrado"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de ingreso invalidos"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    //Actualizar usuarios
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(
    @Parameter(
            description = "id del usuario a actualizar",
            example = "1",
            required = true
    )
    @PathVariable int id,@Valid @RequestBody UsuarioDTO dto){
        UsuarioDTO actualizado = usuarioService.actualizarUsuario(id, dto);
        return ResponseEntity
        .ok(actualizado);
    }

     @Operation(
        summary = "Actualizar usuario",
        description = "Obtener los detalles de un usuario"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Usuario encontrada con exito"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrada"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    //Buscar usuarios por id
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> findById(
        @Parameter(
            description = "id del usuario a buscar",
            example = "1",
            required = true
    )
    @PathVariable int id){
        return ResponseEntity
        .ok(usuarioService.findById(id));
    }
}