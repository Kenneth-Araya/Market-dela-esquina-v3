package cl.duoc.msVenta.controller;

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
import cl.duoc.msVenta.dto.BoletaVentaDTO;
import cl.duoc.msVenta.dto.VentaDTO;
import cl.duoc.msVenta.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @Operation(
        summary = "Listar todos las ventas",
        description = "obtiene una lista con todas las ventas"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de ventas obtenido con exito"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    // Listar ventas
    @GetMapping
    public ResponseEntity<List<VentaDTO>> listarTodos() {
        List<VentaDTO> ventaDTOs = ventaService.listarTodos();
        return ResponseEntity
        .ok(ventaDTOs);
    }
    
    @Operation(
        summary = "registrar venta",
        description = "Crea y guarda una nueva venta en el sistema"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Producto creado con exito"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "datos de la venta ingresados no son validos"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error de servidor"
        )
    })
    // Guardar Venta
    @PostMapping
    public ResponseEntity<VentaDTO> guardarVenta(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "datos de la venta a registrar",
            required = true
        )
        @Valid @RequestBody VentaDTO ventaDTO) {
        VentaDTO creado = ventaService.guardarVenta(ventaDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creado);
    }

    @Operation(
        summary = "eliminar venta",
        description = "Elimina un venta existente mediante su id"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode="204",
            description = "Venta eliminado con exito"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Venta no encontrada"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error en el servidor "
        )
    }) 
    // Eliminar venta
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarVenta(
        @Parameter(
            description = "Id de la venta a eliminar",
            example = "1",
            required = true
    )
    @PathVariable Long id) {
        ventaService.eliminarPorId(id);
        return ResponseEntity
                .noContent()
                .build();
    }

    @Operation(
        summary = "Actualizar Venta",
        description = "Actualizar la información de una venta"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode="200",
            description="Venta actualizada con exito"
        ),
        @ApiResponse(
            responseCode ="404",
            description = "Venta no encontrada"
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
    // Actualizar venta
    @PutMapping("/{id}")
    public ResponseEntity<VentaDTO> actualizarVenta(
        @Parameter(
            description = "Id de la venta a actualizar",
            example = "1",
            required = true
    )
    @PathVariable Long id, @Valid @RequestBody VentaDTO dto) {
        VentaDTO actualizado = ventaService.actualizarVenta(id, dto);
        return ResponseEntity
                .ok(actualizado);
    }

     @Operation(
        summary = "Buscar venta por id",
        description = "Obtener los detalles de una venta por id"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Venta encontrada con exito"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Venta no encontrada"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    // buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<VentaDTO> findById(
        @Parameter(
            description = "Id de la venta para buscar",
            example = "1",
            required = true
    )
    @PathVariable Long id) {
        return ResponseEntity
                .ok(ventaService.findById(id));
    }

    //====================================feign====================================
    
    //listar boletas
    @GetMapping("/boletas")
    public ResponseEntity<List<BoletaVentaDTO>> listarBoletas() {
        return ResponseEntity.ok(ventaService.listarBoletas());
    }

    //buscar una boleta por folio
    @GetMapping("/boletas/{folio}")
    public ResponseEntity<BoletaVentaDTO> obtenerBoleta(@PathVariable String folio) {
        return ResponseEntity.ok(ventaService.obtenerBoletaPorFolio(folio));
    }

    //Crear una boleta
    @PostMapping("/boletas")
    public ResponseEntity<BoletaVentaDTO> crearBoleta(@RequestBody BoletaVentaDTO boletaDTO, @PathVariable String folio) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ventaService.crearBoletaParaVenta(boletaDTO));
    }

    //Actualiza boleta (marcar PAGADA)
    @PutMapping("/boletas/{folio}")
    public ResponseEntity<BoletaVentaDTO> actualizarBoleta(@PathVariable String folio,
            @RequestBody BoletaVentaDTO boletaDTO) {
        return ResponseEntity.ok(ventaService.actualizarEstadoBoleta(folio, boletaDTO));
    }

    //Elimina boleta (venta anulada)
    @DeleteMapping("/boletas/{folio}")
    public ResponseEntity<Void> eliminarBoleta(@PathVariable String folio) {
        ventaService.eliminarBoletaDeVenta(folio);
        return ResponseEntity.noContent().build();
    }
}