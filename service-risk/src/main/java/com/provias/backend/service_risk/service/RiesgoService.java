package com.provias.backend.service_risk.service;

import com.provias.backend.service_risk.model.Riesgo;
import com.provias.backend.service_risk.repository.RiesgoRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class RiesgoService {

    private final RiesgoRepository riesgoRepository;

    public Flux<Riesgo> getAllRiesgos() {
        return riesgoRepository.findAll();
    }

    public Mono<Riesgo> getRiesgoById(ObjectId id) {
        return riesgoRepository.findById(id)
                .switchIfEmpty(Mono.error(new RiesgoNotFoundException("Riesgo no encontrado con id: " + id)));
    }

    public static class RiesgoNotFoundException extends RuntimeException {
        public RiesgoNotFoundException(String message) {
            super(message);
        }
    }

    public Mono<Riesgo> saveRiesgo(Riesgo riesgo) {
        validateRiesgo(riesgo);
        return riesgoRepository.save(riesgo);
    }

    public Mono<Riesgo> updateRiesgo(ObjectId id, Riesgo riesgo) {
        return getRiesgoById(id)
                .flatMap(existingRiesgo -> {
                    validateRiesgo(riesgo);
                    existingRiesgo.setNombre(riesgo.getNombre());
                    existingRiesgo.setDescripcion(riesgo.getDescripcion());
                    existingRiesgo.setNivel(riesgo.getNivel());
                    existingRiesgo.setPlanMitigacion(riesgo.getPlanMitigacion());
                    existingRiesgo.setProbabilidad(riesgo.getProbabilidad());
                    existingRiesgo.setImpacto(riesgo.getImpacto());
                    return riesgoRepository.save(existingRiesgo);
                });
    }

    public Mono<Void> deleteRiesgo(ObjectId id) {
        return validateRiesgoExists(id)
                .flatMap(_ -> riesgoRepository.deleteById(id));
    }

    private Mono<Riesgo> validateRiesgoExists(ObjectId id) {
        return riesgoRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new RiesgoNotFoundException("Riesgo no encontrado con id: " + id));
                    }
                    return riesgoRepository.findById(id);
                });
    }

    private void validateRiesgo(Riesgo riesgo) {
        if (riesgo == null) {
            throw new IllegalArgumentException("El riesgo no puede ser nulo");
        }
        if (riesgo.getNombre() == null || riesgo.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del riesgo no puede ser nulo o vacío");
        }
        if (riesgo.getNivel() == null) {
            throw new IllegalArgumentException("El nivel del riesgo no puede ser nulo");
        }
        if (riesgo.getPlanMitigacion() == null || riesgo.getPlanMitigacion().isEmpty()) {
            throw new IllegalArgumentException("El plan de mitigación no puede ser nulo o vacío");
        }
    }
}
