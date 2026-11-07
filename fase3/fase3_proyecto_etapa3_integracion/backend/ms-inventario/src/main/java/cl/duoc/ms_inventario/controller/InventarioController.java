package cl.duoc.ms_inventario.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cl.duoc.ms_inventario.dto.InventarioDTO;
import cl.duoc.ms_inventario.service.InventarioService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "Inventario", 
    description = "Gestión de stock, descuentos, reportes y consultas de productos"
)
@RestController
@RequestMapping("api/v1/inventario")
@RequiredArgsConstructor
public class InventarioController {
    private final InventarioService service;

    @Operation(
        summary = "consultar stock",
        description = "Consultar el estado y cantidad disponible de un producto en especifico"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode="200",
            description = "Stock consultado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado en inventario"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    // 1. Consultar estado y stock (Para ver si es apto para venta)
    @GetMapping("/{id}")
    public ResponseEntity<InventarioDTO> consultarStock(
        @Parameter(
            description = "Id unico del producto en inventario",
            example = "5",
            required = true
        )
        @PathVariable Long id) {
        return ResponseEntity.ok(service.validarObteniendoStock(id));
    }

    @Operation(
        summary = "descontar stock",
        description= "Reduce la cantidad de unidades en inventario para un producto especifico"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Stock descontado exitosamente"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Parametro invalido o cantidad insuficiente para descontar"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno al procesar el descuento"
        )
    })
    // 2. Descontar stock 
    @PostMapping("/{id}/descontar")
    public ResponseEntity<String> descontarStock(
        @Parameter(
            description = "Id unico del producto",
            example = "5",
            required = true
        )
        @PathVariable Long id, 
        @Parameter(
            description = "Cantidad de unidades a descontar del stock",
            example = "10",
            required = true
        )
        @RequestParam int cantidad) {
        service.descontarStock(id, cantidad);
        return ResponseEntity.ok("Stock actualizado: se descontaron " + cantidad + " unidades");
    }

    @Operation(
        summary = "Listar productos con bajo stock",
        description = "Retorna una lista de productos cuyas cantidades actuales son menores o iguales al límite especificado, útil para alertas de reabastecimiento."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de productos obtenida exitosamente"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor"
        )
    })
    // 3. Reporte de stock bajo (Ejemplo: limite = 5)
    @GetMapping("/bajo/{limite}")
    public ResponseEntity<List<InventarioDTO>> listarBajoStock(
        @Parameter(
            description = "Valor umbral para el reporte de bajo stock (ej. si pones 5, listará productos con 5 unidades o menos)", 
            example = "5", 
            required = true
        )
        @PathVariable int limite) {
        return ResponseEntity.ok(service.listarProductosBajoStock(limite));
    }

    @Operation(
        summary = "Listar todos los productos del inventario",
        description = "Obtiene una lista completa de todos los productos registrados en el sistema de inventario."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de productos obtenida exitosamente"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor"
        )
    })
    //4. listar todos 
    @GetMapping("")
    public ResponseEntity<List<InventarioDTO>> listarTodo() {
        return ResponseEntity.ok(service.listarTodo());

    }

    @Operation(
        summary = "Agregar stock",
        description = "Aumenta la cantidad de unidades en inventario para un producto específico, incrementando su disponibilidad actual."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Stock incrementado exitosamente"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Parámetros inválidos (ej. cantidad negativa)"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Producto no encontrado"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor"
        )
    })
    // 5. Agregar stock 
    @PostMapping("/{id}/agregar")
    public ResponseEntity<String> agregarStock(
        @Parameter(
            description = "ID único del producto", 
            example = "101", 
            required = true
        )
        @PathVariable Long id,
        @Parameter(
            description = "Cantidad de unidades a sumar al inventario", 
            example = "10", 
            required = true
        )
        @RequestParam int cantidad) {
        service.agregarStock(id, cantidad);
        return ResponseEntity.ok("Stock actualizado: se agregaron " + cantidad + " unidades");
    }
    

}
