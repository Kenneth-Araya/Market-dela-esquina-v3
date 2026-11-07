package cl.duoc.ms_producto.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cl.duoc.ms_producto.dto.ProductoDTO;
import cl.duoc.ms_producto.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService service;
    
    @Operation(
        summary = "listar todos los productos",
        description = "obtiene una lista con todos los productos registrados en el sistema"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista de productos obtenida de manera exitosa"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    //Listar productos
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarProductos() {
        List<ProductoDTO> productos = service.listarProductos();
        return ResponseEntity.ok(productos);
    }
    
    @Operation(
        summary = "registrar producto",
        description = "Crea y guarda un nuevo producto en el minimarket"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Producto creado exitosamente en el minimarket"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "datos del producto ingresados no validos"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error en el servidor "
        )
    })
    //Crear producto
    @PostMapping
    public ResponseEntity<ProductoDTO> guardarProducto(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "datos del producto a registrar",
            required = true
        )
        @Valid @RequestBody ProductoDTO productoDto) {
        ProductoDTO creado = service.guardarProducto(productoDto);
        return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(creado);
    }

    @Operation(
        summary = "eliminar producto ",
        description = "Elimina un producto existente del minimarket mediante su id"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode="204",
            description = "Producto eliminado exitosamente del minimarket"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Producto no encontrado"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error en el servidor "
        )
    })   
    //Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(
        @Parameter(
            description = "Id del producto a eliminar",
            example = "1",
            required = true
        )
        @PathVariable Long id){
        service.eliminarProducto(id);
        return ResponseEntity
        .noContent()
        .build();
    }

    @Operation(
        summary = "Actualizar producto ",
        description = "Actualiza la información de un producto existente basándose en su id"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode="200",
            description="Producto actualizado exitosamente en el minimarket"
        ),
        @ApiResponse(
            responseCode ="404",
            description = "Producto no encontrado"
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
    // Actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(
        @Parameter(
            description = "Id del producto a actualizar",
            example = "1",
            required = true
        )
        @PathVariable Long id, 
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Objeto con los datos actualizados del producto",
            required = true
        )
        @Valid @RequestBody ProductoDTO dto) {
        ProductoDTO actualizado = service.actualizarProducto(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @Operation(
        summary = "listar producto por id",
        description = "Obtener los detalles de un producto especifico con el ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Producto encontrado exitosamente"
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
    //Buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> listarProductoPorId(
        @Parameter(
            description = "Id unico del producto a buscar",
            example = "1",
            required = true
        )
        @PathVariable Long id) {
        ProductoDTO producto=service.buscarProducto(id);
        return ResponseEntity
        .ok(producto);
    }

}
