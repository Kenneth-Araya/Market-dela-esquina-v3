package cl.duoc.sistemaMarket.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.sistemaMarket.model.Categoria;
import cl.duoc.sistemaMarket.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada: " + id));
    }

    @Transactional
    public Categoria crear(Categoria categoria) {
        validar(categoria);
        if (categoriaRepository.existsByNombreCategoria(categoria.getNombreCategoria())) {
            throw new IllegalStateException("Ya existe una categoría con ese nombre.");
        }
        categoria.setId(null);
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria actualizar(Long id, Categoria datos) {
        Categoria actual = buscarPorId(id);
        validar(datos);
        actual.setNombreCategoria(datos.getNombreCategoria());
        actual.setDescripcionCategoria(datos.getDescripcionCategoria());
        return categoriaRepository.save(actual);
    }

    @Transactional
    public void eliminar(Long id) {
        categoriaRepository.delete(buscarPorId(id));
    }

    private void validar(Categoria c) {
        if (c.getNombreCategoria() == null || c.getNombreCategoria().isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }
        c.setNombreCategoria(c.getNombreCategoria().trim());
        if (c.getNombreCategoria().length() > 100) {
            throw new IllegalArgumentException("El nombre de la categoría no puede superar los 100 caracteres.");
        }
    }
}
