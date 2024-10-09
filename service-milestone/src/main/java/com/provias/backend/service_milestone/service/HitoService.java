package com.provias.backend.service_milestone.service;

import com.provias.backend.service_milestone.model.Hito;
import com.provias.backend.service_milestone.repository.HitoRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class HitoService {

    private final HitoRepository hitoRepository;

    public Flux<Hito> getAllHitos() {
        return hitoRepository.findAll();
    }

    public Mono<Hito> getHitoById(ObjectId id) {
        return hitoRepository.findById(id)
                .switchIfEmpty(Mono.error(new HitoNotFoundException("Hito no encontrado con id: " + id)));
    }

    public Mono<Hito> saveHito(Hito hito) {
        validateHito(hito);
        return hitoRepository.save(hito);
    }

    public Mono<Hito> updateHito(ObjectId id, Hito hito) {
        return getHitoById(id)
                .flatMap(existingHito -> {
                    validateHito(hito);
                    existingHito.setNombre(hito.getNombre());
                    existingHito.setDescripcion(hito.getDescripcion());
                    existingHito.setFecha(hito.getFecha());
                    return hitoRepository.save(existingHito);
                });
    }

    public Mono<Void> deleteHito(ObjectId id) {
        return validateHitoExists(id)
                .flatMap(_ -> hitoRepository.deleteById(id));
    }

    private Mono<Hito> validateHitoExists(ObjectId id) {
        return hitoRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new HitoNotFoundException("Hito no encontrado con id: " + id));
                    }
                    return hitoRepository.findById(id);
                });
    }

    private void validateHito(Hito hito) {
        if (hito == null) {
            throw new IllegalArgumentException("El hito no puede ser nulo");
        }
        if (hito.getNombre() == null || hito.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del hito no puede ser nulo o vacío");
        }
        if (hito.getFecha() == null) {
            throw new IllegalArgumentException("La fecha del hito no puede ser nula");
        }
    }

    public static class HitoNotFoundException extends RuntimeException {
        public HitoNotFoundException(String message) {
            super(message);
        }
    }
}
