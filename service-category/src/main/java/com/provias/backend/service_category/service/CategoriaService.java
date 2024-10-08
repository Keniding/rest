package com.provias.backend.service_category.service;
import com.provias.backend.service_category.model.Categoria;
import com.provias.backend.service_category.repository.CategoriaRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public Flux<Categoria> getAllCategorias() {
        return categoriaRepository.findAll();
    }

    public Mono<Categoria> getCategoriaById(ObjectId id) {
        return categoriaRepository.findById(id)
                .switchIfEmpty(Mono.error(new CategoriaNotFoundException("Categoría no encontrada con id: " + id)));
    }

    public Mono<Categoria> saveCategoria(Categoria categoria) {
        validateCategoria(categoria);
        return categoriaRepository.save(categoria);
    }

    public Mono<Categoria> updateCategoria(ObjectId id, Categoria categoria) {
        return getCategoriaById(id)
                .flatMap(existingCategoria -> {
                    validateCategoria(categoria);
                    existingCategoria.setNombre(categoria.getNombre());
                    existingCategoria.setDescripcion(categoria.getDescripcion());
                    return categoriaRepository.save(existingCategoria);
                });
    }

    public Mono<Void> deleteCategoria(ObjectId id) {
        return validateCategoriaExists(id)
                .flatMap(_ -> categoriaRepository.deleteById(id));
    }

    private Mono<Categoria> validateCategoriaExists(ObjectId id) {
        return categoriaRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new CategoriaNotFoundException("Categoría no encontrada con id: " + id));
                    }
                    return categoriaRepository.findById(id);
                });
    }

    private void validateCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría no puede ser nula");
        }
        if (categoria.getNombre() == null || categoria.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede ser nulo o vacío");
        }
    }

    public static class CategoriaNotFoundException extends RuntimeException {
        public CategoriaNotFoundException(String message) {
            super(message);
        }
    }
}
