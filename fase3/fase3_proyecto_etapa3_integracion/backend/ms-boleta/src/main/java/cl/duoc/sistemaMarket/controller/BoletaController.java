package cl.duoc.sistemaMarket.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cl.duoc.sistemaMarket.dto.BoletaDTO;
import cl.duoc.sistemaMarket.service.BoletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/boletas")
@RequiredArgsConstructor
public class BoletaController {

    private final BoletaService boletaService;

    @Operation(
        summary = "Listar todos las boletas",
        description = "obtiene una lista con todas las boletas"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de boletas obtenido con exito"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    //Listar todos
    @GetMapping()
    public ResponseEntity<List<BoletaDTO>> listarTodos() {
        List<BoletaDTO> boletaDTOs = boletaService.listarTodos();
        return ResponseEntity
        .ok(boletaDTOs);
    }

    //guardar boleta
    @PostMapping()
    public ResponseEntity<BoletaDTO> guardarBoleta(@Valid @RequestBody BoletaDTO boletaDTO) {
        BoletaDTO guardado = boletaService.guardarBoleta(boletaDTO);
        return ResponseEntity.ok(guardado);
    }

    @Operation(
        summary = "Eliminar boleta",
        description = "Elimina una boleta existente mediante su id"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Boleta eliminado con exito"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Venta no encontrada"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error de servidor"
        )
    })
    //Eliminar boleta
    @DeleteMapping("/{folio}")
    public ResponseEntity<Void> eliminarBoleta(
        @Parameter(
            description = "Folio de la boleta a eliminar",
            example = "1",
            required = true
    )
    @PathVariable String folio) {
        boletaService.eliminarBoleta(folio);
        return ResponseEntity
        .noContent()
        .build();
    }
    
    @Operation(
        summary = "Actualizar Boleta",
        description = "Actualizar la información de una boleta"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode="200",
            description="Boleta actualizada con exito"
        ),
        @ApiResponse(
            responseCode ="404",
            description = "Boleta no encontrada"
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
    //Actualizar boleta
    @PutMapping("/{folio}")
    public ResponseEntity<BoletaDTO> actualizaBoleta(
        @Parameter(
            description = "Folio de la boleta a actualizar",
            example = "1",
            required = true
    )
    @PathVariable String folio, @Valid @RequestBody BoletaDTO boletaDTO) {
        boletaService.actualizarBoleta(folio, boletaDTO);
        return ResponseEntity
        .ok(boletaDTO);
    }

    @Operation(
        summary = "Buscar boleta por id",
        description = "Obtener los detalles de una boleta por id"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Boleta encontrada con exito"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Boleta no encontrada"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    //Obtener boleta por folio
    @GetMapping("/{folio}")
    public ResponseEntity<BoletaDTO> obtenerBoletaPorFolio(
        @Parameter(
            description = "Folio de la boleta para buscar",
            example = "1",
            required = true
    )@PathVariable String folio) {
        return ResponseEntity
        .ok(boletaService.obtenerBoletaPorFolio(folio));
    }
}