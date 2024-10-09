package com.provias.backend.service_post.service;

import com.provias.backend.service_post.model.Cargo;
import com.provias.backend.service_post.repository.CargoRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class CargoService {

    private final CargoRepository cargoRepository;

    public Flux<Cargo> getAllCargos() {
        return cargoRepository.findAll();
    }

    public Mono<Cargo> getCargoById(ObjectId id) {
        return cargoRepository.findById(id)
                .switchIfEmpty(Mono.error(new CargoNotFoundException("Cargo no encontrado con id: " + id)));
    }

    public Mono<Cargo> saveCargo(Cargo cargo) {
        validateCargo(cargo);
        return cargoRepository.save(cargo);
    }

    public Mono<Cargo> updateCargo(ObjectId id, Cargo cargo) {
        return getCargoById(id)
                .flatMap(existingCargo -> {
                    validateCargo(cargo);
                    existingCargo.setNombre(cargo.getNombre());
                    existingCargo.setDescripcion(cargo.getDescripcion());
                    existingCargo.setRango(cargo.getRango());
                    existingCargo.setDepartamento(cargo.getDepartamento());

                    return cargoRepository.save(existingCargo);
                });
    }

    public Mono<Void> deleteCargo(ObjectId id) {
        return validateCargoExists(id)
                .flatMap(_ -> cargoRepository.deleteById(id));
    }

    private Mono<Cargo> validateCargoExists(ObjectId id) {
        return cargoRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new CargoNotFoundException("Cargo no encontrado con id: " + id));
                    }
                    return cargoRepository.findById(id);
                });
    }

    private void validateCargo(Cargo cargo) {
        if (cargo == null) {
            throw new IllegalArgumentException("El cargo no puede ser nulo");
        }
        if (cargo.getNombre() == null || cargo.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cargo no puede ser nulo o vacío");
        }
    }

    public static class CargoNotFoundException extends RuntimeException {
        public CargoNotFoundException(String message) {
            super(message);
        }
    }
}
