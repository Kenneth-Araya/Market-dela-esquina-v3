package cl.duoc.sistemaMarket.service;

import cl.duoc.sistemaMarket.model.Categoria;
import cl.duoc.sistemaMarket.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoriaMock;

    @BeforeEach
    void setUp() {
        categoriaMock = new Categoria(1L, "Despensa", "Productos basicos");
    }

    @Test
    void listar_RetornaListaCategorias() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoriaMock));

        List<Categoria> resultado = categoriaService.listar();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorId_Existe_RetornaCategoria() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaMock));

        Categoria resultado = categoriaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Despensa", resultado.getNombreCategoria());
    }

    @Test
    void buscarPorId_NoExiste_LanzaExcepcion() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> categoriaService.buscarPorId(99L));
    }

    @Test
    void crear_DatosValidos_GuardaCategoria() {
        when(categoriaRepository.existsByNombreCategoria("Despensa")).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaMock);

        Categoria resultado = categoriaService.crear(categoriaMock);

        assertNotNull(resultado);
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    void crear_NombreDuplicado_LanzaExcepcion() {
        when(categoriaRepository.existsByNombreCategoria("Despensa")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> categoriaService.crear(categoriaMock));
    }

    @Test
    void crear_NombreVacio_LanzaExcepcion() {
        categoriaMock.setNombreCategoria("");

        assertThrows(IllegalArgumentException.class, () -> categoriaService.crear(categoriaMock));
    }

    @Test
    void crear_NombreSuperaLimite_LanzaExcepcion() {
        categoriaMock.setNombreCategoria("a".repeat(101));

        assertThrows(IllegalArgumentException.class, () -> categoriaService.crear(categoriaMock));
    }

    @Test
    void actualizar_Existe_ActualizaCategoria() {
        Categoria datos = new Categoria(null, "Lacteos", "Leches y quesos");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaMock));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaMock);

        Categoria resultado = categoriaService.actualizar(1L, datos);

        assertEquals("Lacteos", resultado.getNombreCategoria());
        verify(categoriaRepository, times(1)).save(categoriaMock);
    }

    @Test
    void eliminar_Existe_EliminaCategoria() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaMock));

        categoriaService.eliminar(1L);

        verify(categoriaRepository, times(1)).delete(categoriaMock);
    }
}