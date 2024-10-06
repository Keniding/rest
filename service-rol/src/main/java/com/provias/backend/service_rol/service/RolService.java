package com.provias.backend.service_rol.service;

import com.provias.backend.service_rol.model.Rol;
import com.provias.backend.service_rol.repository.RolRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    public Flux<Rol> getAllRoles() {
        return rolRepository.findAll();
    }

    public Mono<Rol> getRolById(ObjectId id) {
        return rolRepository.findById(id);
    }

    public Mono<Rol> createRol(Rol rol) {
        return rolRepository.save(rol);
    }

    public Mono<Rol> updateRol(ObjectId id, Rol rolDetails) {
        return rolRepository.findById(id)
                .flatMap(rol -> {
                    rol.setName(rolDetails.getName());
                    rol.setDescription(rolDetails.getDescription());
                    return rolRepository.save(rol);
                });
    }

    public Mono<Void> deleteRol(ObjectId id) {
        return rolRepository.deleteById(id);
    }
}
