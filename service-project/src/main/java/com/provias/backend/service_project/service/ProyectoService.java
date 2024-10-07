package com.provias.backend.service_project.service;

import com.provias.backend.service_project.model.Proyecto;
import com.provias.backend.service_project.repository.ProyectoRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;

    public Flux<Proyecto> getAllProyectos() {
        return proyectoRepository.findAll();
    }

    public Mono<Proyecto> getProyectoById(ObjectId id) {
        return proyectoRepository.findById(id)
                .switchIfEmpty(Mono.error(new ProyectoNotFoundException("Proyecto no encontrado con id: " + id)));
    }

    public Mono<Proyecto> saveProyecto(Proyecto proyecto) {
        validateProyecto(proyecto);
        return proyectoRepository.save(proyecto);
    }

    public Mono<Proyecto> updateProyecto(ObjectId id, Proyecto proyecto) {
        return getProyectoById(id)
                .flatMap(existingProyecto -> {
                    validateProyecto(proyecto);
                    existingProyecto.setNombre(proyecto.getNombre());
                    existingProyecto.setDescripcion(proyecto.getDescripcion());
                    existingProyecto.setCantidadKm(proyecto.getCantidadKm());
                    existingProyecto.setTipo(proyecto.getTipo());
                    existingProyecto.setUbicacion(proyecto.getUbicacion());
                    existingProyecto.setFechaInicio(proyecto.getFechaInicio());
                    existingProyecto.setFechaFin(proyecto.getFechaFin());

                    return proyectoRepository.save(existingProyecto);
                });
    }

    public Mono<Void> deleteProyecto(ObjectId id) {
        return validateProyectoExists(id)
                .flatMap(_ -> proyectoRepository.deleteById(id));
    }

    private Mono<Proyecto> validateProyectoExists(ObjectId id) {
        return proyectoRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new ProyectoNotFoundException("Proyecto no encontrado con id: " + id));
                    }
                    return proyectoRepository.findById(id);
                });
    }

    private void validateProyecto(Proyecto proyecto) {
        if (proyecto == null) {
            throw new IllegalArgumentException("El proyecto no puede ser nulo");
        }
        if (proyecto.getNombre() == null || proyecto.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proyecto no puede ser nulo o vacío");
        }

    }

    public static class ProyectoNotFoundException extends RuntimeException {
        public ProyectoNotFoundException(String message) {
            super(message);
        }
    }
}
