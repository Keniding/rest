package com.provias.backend.service_type.service;

import com.provias.backend.service_type.model.Tipo;
import com.provias.backend.service_type.repository.TipoRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class TipoService {

    private final TipoRepository tipoRepository;

    public Flux<Tipo> getAllTipos() {
        return tipoRepository.findAll();
    }

    public Mono<Tipo> getTipoById(ObjectId id) {
        return tipoRepository.findById(id)
                .switchIfEmpty(Mono.error(new TipoNotFoundException("Tipo no encontrado con id: " + id)));
    }

    public Mono<Tipo> saveTipo(Tipo tipo) {
        validateTipo(tipo);
        return tipoRepository.save(tipo);
    }

    public Mono<Tipo> updateTipo(ObjectId id, Tipo tipo) {
        return getTipoById(id)
                .flatMap(existingTipo -> {
                    validateTipo(tipo);
                    existingTipo.setNombre(tipo.getNombre());
                    existingTipo.setDescripcion(tipo.getDescripcion());
                    existingTipo.setSubcategorias(tipo.getSubcategorias());

                    return tipoRepository.save(existingTipo);
                });
    }

    public Mono<Void> deleteTipo(ObjectId id) {
        return validateTipoExists(id)
                .flatMap(_ -> tipoRepository.deleteById(id));
    }

    private Mono<Tipo> validateTipoExists(ObjectId id) {
        return tipoRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new TipoNotFoundException("Tipo no encontrado con id: " + id));
                    }
                    return tipoRepository.findById(id);
                });
    }

    private void validateTipo(Tipo tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo no puede ser nulo");
        }
        if (tipo.getNombre() == null || tipo.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del tipo no puede ser nulo o vacío");
        }
    }

    public static class TipoNotFoundException extends RuntimeException {
        public TipoNotFoundException(String message) {
            super(message);
        }
    }
}
