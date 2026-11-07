package cl.duoc.ms_producto.controller;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;

import java.util.Arrays;
import java.util.List;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import cl.duoc.ms_producto.dto.ProductoDTO;
import cl.duoc.ms_producto.exception.ManejadorGlobalExcepciones;
import cl.duoc.ms_producto.service.ProductoService;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService servicio;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void listarProductos_DeberiaRetornarListaDeProductosYStatus200() throws Exception{
        //GIVEN PREPARAMOS LOS DATOS QUE DEVOLVERA EL SERVICIO FALSO
        ProductoDTO p1=new ProductoDTO();
        p1.setNombre("Atun");

        ProductoDTO p2=new ProductoDTO();
        p2.setNombre("Piña");

        List<ProductoDTO> listaSimulada=Arrays.asList(p1,p2);

        //LE DECIMOS AL MOCK QUE HACER CUANDO EL CONTROLADOR LO LLAME
        when(servicio.listarProductos()).thenReturn(listaSimulada);

        //WHEN Y THEN HACEMOS LA PETICION GET A LA URL Y VERIFICAMOS LA RESPUESTA
        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk()) //SE VERIFICA LA RESPUESTA HTTP 200
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Atun"))
                .andExpect(jsonPath("$[1].nombre").value("Piña"));
        
    }

    @Test
    void guardarProducto_DeberiaRetornarStatus201YProductoCreado() throws Exception{
        //GIVEN PREPARAMOS EL DTO QUE ENVIAREMOS
        ProductoDTO nuevoProducto=new ProductoDTO();
        nuevoProducto.setNombre("Coca-Cola");
        nuevoProducto.setPrecioProducto(1500.0);
        nuevoProducto.setCodigoBarra("1234567890123"); // 13 caracteres máximo
        nuevoProducto.setDescripcion("Bebida gaseosa de 2 litros");
        nuevoProducto.setCategoriaId(1L);
        //SIMULAMOS QUE EL SERVICIO RECIBIO CUALQUIER DTO Y DEVOLVER EL PRODUCTO CON ID
        when(servicio.guardarProducto(any(ProductoDTO.class))).thenReturn(nuevoProducto);

        //WHEN Y THEN SIMULAMOS LA PETICION POST
        mockMvc.perform(post("/api/v1/productos")
                .contentType(MediaType.APPLICATION_JSON)//AVISAMOS EL ENVIO DE UN JSON
                .content(objectMapper.writeValueAsString(nuevoProducto)))//CONVERTIMOS EL OBJETO A JSON
                .andDo(print())
                .andExpect(status().isCreated())//VERIFICAMOS EL CODIGO 201
                .andExpect(jsonPath("$.nombre").value("Coca-Cola"));

    }

    @Test
    void eliminarProducto_DeberiaRetornarStatus204() throws Exception {
        //GIVEN SIMULAMOS QUE EL SERVIDOR ELIMINA CORRECTAMENTE
        Long id=1L;
        doNothing().when(servicio).eliminarProducto(id);

        //WHEN Y THEN SIMULAMOS LA PETICIO DELETE
        mockMvc.perform(delete("/api/v1/productos/{id}", id))//PASAMOS EL ID EN LA URL
                .andExpect(status().isNoContent());//VERIFICAMOS EL 204
    }

    @Test
    void eliminarProducto_CuandoServicioLanzaIllegalArgument_DeberiaRetornar400() throws Exception {
        // GIVEN: Forzamos al servicio a lanzar IllegalArgumentException
        doThrow(new IllegalArgumentException("ID inválido")).when(servicio).eliminarProducto(anyLong());

        // WHEN: Llamamos al endpoint
        mockMvc.perform(delete("/api/v1/productos/1"))
               // THEN: Verificamos que el manejador lo atrapó y devolvió 400
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("Error de validación"))
               .andExpect(jsonPath("$.mensaje").value("ID inválido"));
    }

    @Test
    void actualizarProducto_DeberiaRetornarStatus200ProductoActualizado() throws Exception{
        //GIVEN EL ID A ACTUALIZAR Y EL DTO CON LOS DATOS NUEVOS
        Long id=1L;
        ProductoDTO dtoActualizado=new ProductoDTO();
        dtoActualizado.setNombre("Coca-Cola Zero");
        dtoActualizado.setPrecioProducto(1500.0);
        dtoActualizado.setCodigoBarra("1234567890123");
        dtoActualizado.setDescripcion("Bebida sin azúcar");
        dtoActualizado.setCategoriaId(1L);

        //SIMULAMOS QUE EL SERVICIO RETORNA EL PRODUCTO ACTUALIZADO
        when(servicio.actualizarProducto(eq(id), any(ProductoDTO.class))).thenReturn(dtoActualizado);

        //WHEN Y THEN PETICION PUT CON ID EN LA URL Y DTO EN EL CUERPO
        mockMvc.perform(put("/api/v1/productos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Coca-Cola Zero"));
    }

    @Test
    void listarProductoPorId_DeberiaRetornarStatus200YProducto() throws Exception{
        Long id=1L;
        ProductoDTO producto=new ProductoDTO();
        producto.setNombre("Coca-Cola");
        producto.setCodigoBarra("1234567890123");
        producto.setPrecioProducto(2000.0);
        producto.setDescripcion("Bebida coca cola original 1.5L");
        producto.setCategoriaId(1L);

        //SIMULAMOS QUE EL SERVICIO ENCUENTRA EL PRODUCTO CON ESE ID
        when(servicio.buscarProducto(id)).thenReturn(producto);

        //WHEN Y THEN PETICION GET AL ENDPOINT CON EL ID
        mockMvc.perform(get("/api/v1/productos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Coca-Cola"))
                .andExpect(jsonPath("$.codigoBarra").value("1234567890123"));
    }

    // 1. Cubre: handleRecursoNoEncontrado (404)
    @Test
    void listarProductoPorId_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        Long id = 99L;
        // Simulamos que el servicio lanza nuestra excepción personalizada
        when(servicio.buscarProducto(id)).thenThrow(new cl.duoc.ms_producto.exception.RecursoNoEncontradoException("No existe"));

        mockMvc.perform(get("/api/v1/productos/{id}", id))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("Recurso no encontrado"));
    }

    // 2. Cubre: handleIllegalArgumentException (400)
    @Test
    void guardarProducto_CuandoDTOEsInvalido_DeberiaRetornar400() throws Exception {
        // 1. Preparamos un DTO que no pase las validaciones (ej: nombre vacío)
        ProductoDTO dtoInvalido = new ProductoDTO();
        dtoInvalido.setNombre(""); // Asumiendo que tienes @NotBlank en tu DTO
        // 3. Ejecutamos el post
        mockMvc.perform(post("/api/v1/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoInvalido)))
                // 4. Verificamos que el manejador de validaciones atrapó el error
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").exists()); // Verifica que el campo inválido esté en el mapa
    }

    // 3. Cubre: handleGenericException (500)
    @Test
    void listarProductos_CuandoFallaServidor_DeberiaRetornar500() throws Exception {
        when(servicio.listarProductos()).thenThrow(new RuntimeException("Error inesperado"));

        mockMvc.perform(get("/api/v1/productos"))
               .andExpect(status().isInternalServerError());
    }

    @Test
    void listarProductoPorId_CuandoIdNoEsNumero_DeberiaRetornar400() throws Exception {
        // Al enviar "abc" en un PathVariable Long, Spring lanza automáticamente el MethodArgumentTypeMismatchException
        mockMvc.perform(get("/api/v1/productos/abc"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("Parametro invalido"));
    }

    @Test
    void testErrorResponseGettersSetters() {
        // Instanciamos el objeto
        ManejadorGlobalExcepciones.ErrorResponse error = 
            new ManejadorGlobalExcepciones.ErrorResponse("Error inicial", "Mensaje inicial");

        // Probamos los Setters
        error.setError("Error cambiado");
        error.setMensaje("Mensaje cambiado");

        // Probamos los Getters
        assertEquals("Error cambiado", error.getError());
        assertEquals("Mensaje cambiado", error.getMensaje());
    }



}