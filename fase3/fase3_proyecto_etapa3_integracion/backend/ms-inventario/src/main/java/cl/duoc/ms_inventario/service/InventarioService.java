package cl.duoc.ms_inventario.service;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cl.duoc.ms_inventario.clients.ProductoClient;
import cl.duoc.ms_inventario.dto.InventarioDTO;
import cl.duoc.ms_inventario.dto.InventarioMapper;
import cl.duoc.ms_inventario.dto.ProductoResponseDTO;
import cl.duoc.ms_inventario.exception.RecursoNoEncontradoException;
import cl.duoc.ms_inventario.model.Inventario;
import cl.duoc.ms_inventario.repository.InventarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class InventarioService {
    private final InventarioRepository repo;
    private final InventarioMapper mapper;
    private final ProductoClient productoClient;

    public InventarioDTO validarObteniendoStock(Long idProducto){
    log.info("Iniciando validacion de stock para el producto: {}", idProducto);

    Inventario inventario = repo.findByIdProducto(idProducto)
            .orElseThrow(() -> new RecursoNoEncontradoException("El producto no existe en el inventario"));

    InventarioDTO dto = mapper.toDTO(inventario);

    // Simplificamos la lógica: solo validamos si hay stock o no
    if (inventario.getStockActual() <= 0) {
        dto.setEstadoProducto("SIN STOCK");
    } else {
        dto.setEstadoProducto("DISPONIBLE");
    }

    try {
        log.info("Cruzando puente Feign hacia ms-producto para el id: {}", idProducto);
        ProductoResponseDTO productoDto = productoClient.obtenerProductoPorId(idProducto);
        if (productoDto != null) {
            log.info("Feign funciono correctamente Seteando nombre del producto en el DTO");
            dto.setNombreProducto(productoDto.getNombre());
        }
    } catch (Exception e) {
        log.error("No se pudo conectar con ms-producto a través de Feign: {}", e.getMessage());
    }

    return dto;
    }

    @Transactional
    public void descontarStock(Long idProducto, int cantidad){
        log.info("procesando salida de {} unidades para el producto {} ", cantidad, idProducto);

        // 1. EL ESCUDO PROTECTOR: Bloqueamos números negativos y el cero
        if (cantidad <= 0) {
            log.error("Falla en salida: Intento de descontar cantidad invalida ({})", cantidad);
            throw new IllegalArgumentException("La cantidad a descontar debe ser mayor a cero");
        }

        // 2. Buscamos el producto
        Inventario inv=repo.findByIdProducto(idProducto)
                .orElseThrow(()->new RecursoNoEncontradoException("NO SE PUEDE DESCONTAR: PRODUCTO NO ENCONTRADO"));
        
        // 3. Validamos que alcance el stock
        if (inv.getStockActual()<cantidad){
            log.error("Falla en salida: Stock insuficiente para el producto {} ",idProducto);
            throw new RuntimeException("Stock insuficiente para realizar la venta");
        }
        
        // 4. Hacemos la matemática y guardamos
        inv.setStockActual(inv.getStockActual()-cantidad);
        repo.save(inv);
        log.info("Salida exitosa el nuevo stock es: {}",inv.getStockActual());
    }

    @Transactional
    public void agregarStock(Long idProducto, int cantidad){
        log.info("Procesando entrada de {} unidades para el producto {}", cantidad, idProducto);

        // 1. Validamos que no intenten sumar números negativos o cero (para evitar que nos roben stock con un método de suma)
        if (cantidad <= 0) {
            log.error("Falla en entrada: Intento de agregar cantidad invalida ({})", cantidad);
            throw new IllegalArgumentException("La cantidad a agregar debe ser mayor a cero");
        }

        // 2. Buscamos el producto en la BD
        Inventario inv = repo.findByIdProducto(idProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException("NO SE PUEDE AGREGAR STOCK: PRODUCTO NO ENCONTRADO"));
        
        // 3. Sumamos la cantidad y guardamos
        inv.setStockActual(inv.getStockActual() + cantidad);
        repo.save(inv);
        log.info("Entrada exitosa, el nuevo stock es: {}", inv.getStockActual());
    }

    
    public List<InventarioDTO>listarProductosBajoStock(int limite){
        log.info("Buscando productos con un stock menor a {} ",limite);
        // 1. Traemos todo y filtramos en memoria por el límite dinámico
        List<InventarioDTO> listaDtos = repo.findAll().stream()
                .filter(i -> i.getStockActual() <= limite)
                .map(mapper::toDTO)
                .collect(Collectors.toList());

        listaDtos.forEach(dto -> {
            try {
                ProductoResponseDTO productoDto = productoClient.obtenerProductoPorId(dto.getIdProducto());
                if (productoDto != null) {
                    dto.setNombreProducto(productoDto.getNombre());
                }
            } catch (Exception e) {
                log.error("Error Feign al traer nombre para el producto ID {}: {}", dto.getIdProducto(), e.getMessage());
                dto.setNombreProducto("Nombre no disponible");
            }
        });

        return listaDtos;
    }

    public List<InventarioDTO> listarTodo() {
        log.info("Buscando todo el inventario registrado");
        
        // 1. Traemos todo de la BD y lo pasamos a DTO
        List<InventarioDTO> listaDtos = repo.findAll().stream()
                .map(mapper::toDTO)
                .sorted(Comparator.comparing(InventarioDTO::getIdProducto))
                .collect(Collectors.toList());

        listaDtos.forEach(dto -> {
            if (dto.getStockActual() > 0) {
                dto.setEstadoProducto("DISPONIBLE");
            } else {
                dto.setEstadoProducto("SIN STOCK");
            }

            try {
                ProductoResponseDTO productoDto = productoClient.obtenerProductoPorId(dto.getIdProducto());
                if (productoDto != null) {
                    dto.setNombreProducto(productoDto.getNombre());
                }
            } catch (Exception e) {
                log.error("Error Feign al traer nombre para el producto ID {}: {}", dto.getIdProducto(), e.getMessage());
                dto.setNombreProducto("Nombre no disponible");
            }
        });

        return listaDtos;
    }
        
      
    
    




}
