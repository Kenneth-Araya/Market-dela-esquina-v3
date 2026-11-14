package cl.duoc.sistemaMarket.config;

import cl.duoc.sistemaMarket.model.Categoria;
import cl.duoc.sistemaMarket.model.Inventario;
import cl.duoc.sistemaMarket.model.Producto;
import cl.duoc.sistemaMarket.repository.CategoriaRepository;
import cl.duoc.sistemaMarket.repository.InventarioRepository;
import cl.duoc.sistemaMarket.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatosInicialesTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private DatosIniciales datosIniciales;

    @Test
    void run_BaseVacia_CargaDatosIniciales() {
        when(productoRepository.count()).thenReturn(0L);
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(inv -> {
            Categoria c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        datosIniciales.run();

        verify(categoriaRepository, times(4)).save(any(Categoria.class));
        verify(productoRepository, times(6)).save(any(Producto.class));
        verify(inventarioRepository, times(6)).save(any(Inventario.class));
    }

    @Test
    void run_ConProductosExistentes_NoCargaNada() {
        when(productoRepository.count()).thenReturn(5L);

        datosIniciales.run();

        verify(categoriaRepository, never()).save(any());
        verify(productoRepository, never()).save(any());
    }
}