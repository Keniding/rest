package com.provias.backend.service_staff.service;

import com.provias.backend.service_staff.model.Personal;
import com.provias.backend.service_staff.repository.CargoRepository;
import com.provias.backend.service_staff.repository.PersonalRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class PersonalService {

    private final PersonalRepository personalRepository;
    private final CargoRepository cargoRepository;

    public Flux<Personal> getAllPersonal() {
        return personalRepository.findAll();
    }

    public Mono<Personal> getPersonalById(ObjectId id) {
        return personalRepository.findById(id)
                .switchIfEmpty(Mono.error(new PersonalNotFoundException("Personal not found with id: " + id)));
    }

    public static class PersonalNotFoundException extends RuntimeException {
        public PersonalNotFoundException(String message) {
            super(message);
        }
    }

    public Mono<Personal> savePersonal(Personal personal) {
        validatePersonal(personal);
        return cargoRepository.findById(personal.getCargo().getId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Cargo not found with id: " + personal.getCargo().getId())))
                .then(personalRepository.save(personal));
    }

    public Mono<Personal> updatePersonal(ObjectId id, Personal personal) {
        return getPersonalById(id)
                .flatMap(existingPersonal -> {
                    validatePersonal(personal);
                    existingPersonal.setNombre(personal.getNombre());
                    existingPersonal.setTelefono(personal.getTelefono());
                    existingPersonal.setCorreo(personal.getCorreo());
                    existingPersonal.setCargo(personal.getCargo());

                    return cargoRepository.findById(personal.getCargo().getId())
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("Cargo not found with id: " + personal.getCargo().getId())))
                            .then(personalRepository.save(existingPersonal));
                });
    }

    public Mono<Void> deletePersonal(ObjectId id) {
        return validatePersonalExists(id)
                .flatMap(_ -> personalRepository.deleteById(id));
    }

    private Mono<Personal> validatePersonalExists(ObjectId id) {
        return personalRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new PersonalNotFoundException("Personal not found with id: " + id));
                    }
                    return personalRepository.findById(id);
                });
    }

    private void validatePersonal(Personal personal) {
        if (personal == null) {
            throw new IllegalArgumentException("Personal cannot be null");
        }

        if (personal.getNombre() == null || personal.getNombre().isEmpty()) {
            throw new IllegalArgumentException("Nombre cannot be null or empty");
        }
        if (personal.getTelefono() == null || personal.getTelefono().isEmpty()) {
            throw new IllegalArgumentException("Telefono cannot be null or empty");
        }
        if (personal.getCorreo() == null || personal.getCorreo().isEmpty()) {
            throw new IllegalArgumentException("Correo cannot be null or empty");
        }
    }
}
