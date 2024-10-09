package com.provias.backend.service_activity.service;

import com.provias.backend.service_activity.model.Actividad;
import com.provias.backend.service_activity.repository.ActividadRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
@AllArgsConstructor
public class ActividadService {

    private final ActividadRepository actividadRepository;

    public Flux<Actividad> getAllActividades() {
        return actividadRepository.findAll();
    }

    public Mono<Actividad> getActividadById(ObjectId id) {
        return actividadRepository.findById(id)
                .switchIfEmpty(Mono.error(new ActividadNotFoundException("Actividad no encontrada con id: " + id)));
    }

    public Mono<Actividad> saveActividad(Actividad actividad) {
        validateActividad(actividad);
        return actividadRepository.save(actividad);
    }

    public Mono<Actividad> updateActividad(ObjectId id, Actividad actividad) {
        return getActividadById(id)
                .flatMap(existingActividad -> {
                    validateActividad(actividad);
                    existingActividad.setNombre(actividad.getNombre());
                    existingActividad.setDescripcion(actividad.getDescripcion());
                    existingActividad.setFechaInicio(actividad.getFechaInicio());
                    existingActividad.setFechaFin(actividad.getFechaFin());
                    existingActividad.setEstado(actividad.getEstado());
                    return actividadRepository.save(existingActividad);
                });
    }

    public Mono<Void> deleteActividad(ObjectId id) {
        return validateActividadExists(id)
                .flatMap(_ -> actividadRepository.deleteById(id));
    }

    private Mono<Actividad> validateActividadExists(ObjectId id) {
        return actividadRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new ActividadNotFoundException("Actividad no encontrada con id: " + id));
                    }
                    return actividadRepository.findById(id);
                });
    }

    private void validateActividad(Actividad actividad) {
        if (actividad == null) {
            throw new IllegalArgumentException("La actividad no puede ser nula");
        }
        if (actividad.getNombre() == null || actividad.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la actividad no puede ser nulo o vacío");
        }
        if (actividad.getFechaInicio() == null || actividad.getFechaFin() == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin no pueden ser nulas");
        }
        if (actividad.getEstado() == null) {
            throw new IllegalArgumentException("El estado de la actividad no puede ser nulo");
        }
    }

    public static class ActividadNotFoundException extends RuntimeException {
        public ActividadNotFoundException(String message) {
            super(message);
        }
    }
}
