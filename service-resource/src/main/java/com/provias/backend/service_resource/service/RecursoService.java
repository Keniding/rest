package com.provias.backend.service_resource.service;

import com.provias.backend.service_resource.model.Recurso;
import com.provias.backend.service_resource.repository.RecursoRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class RecursoService {

    private final RecursoRepository recursoRepository;

    public Flux<Recurso> getAllRecursos() {
        return recursoRepository.findAll();
    }

    public Mono<Recurso> getRecursoById(ObjectId id) {
        return recursoRepository.findById(id)
                .switchIfEmpty(Mono.error(new RecursoNotFoundException("Recurso no encontrado con id: " + id)));
    }

    public Mono<Recurso> saveRecurso(Recurso recurso) {
        validateRecurso(recurso);
        return recursoRepository.save(recurso);
    }

    public Mono<Recurso> updateRecurso(ObjectId id, Recurso recurso) {
        return getRecursoById(id)
                .flatMap(existingRecurso -> {
                    validateRecurso(recurso);
                    existingRecurso.setNombre(recurso.getNombre());
                    existingRecurso.setDescripcion(recurso.getDescripcion());
                    existingRecurso.setCosto(recurso.getCosto());
                    existingRecurso.setUnidad(recurso.getUnidad());
                    existingRecurso.setCantidadDisponible(recurso.getCantidadDisponible());
                    existingRecurso.setFechaAdquisicion(recurso.getFechaAdquisicion());
                    existingRecurso.setProveedor(recurso.getProveedor());

                    return recursoRepository.save(existingRecurso);
                });
    }

    public Mono<Void> deleteRecurso(ObjectId id) {
        return validateRecursoExists(id)
                .flatMap(_ -> recursoRepository.deleteById(id));
    }

    private Mono<Recurso> validateRecursoExists(ObjectId id) {
        return recursoRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new RecursoNotFoundException("Recurso no encontrado con id: " + id));
                    }
                    return recursoRepository.findById(id);
                });
    }

    private void validateRecurso(Recurso recurso) {
        if (recurso == null) {
            throw new IllegalArgumentException("El recurso no puede ser nulo");
        }
        if (recurso.getNombre() == null || recurso.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del recurso no puede ser nulo o vacío");
        }
    }

    public static class RecursoNotFoundException extends RuntimeException {
        public RecursoNotFoundException(String message) {
            super(message);
        }
    }
}
