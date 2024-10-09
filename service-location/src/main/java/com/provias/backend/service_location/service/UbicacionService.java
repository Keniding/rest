package com.provias.backend.service_location.service;

import com.provias.backend.service_location.model.Ubicacion;
import com.provias.backend.service_location.repository.UbicacionRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UbicacionService {

    private final UbicacionRepository ubicacionRepository;
    private final GeocodingService geocodingService;

    public Flux<Ubicacion> getAllLocations() {
        return ubicacionRepository.findAll();
    }

    public Mono<Ubicacion> getLocationById(ObjectId id) {
        return ubicacionRepository.findById(id)
                .switchIfEmpty(Mono.error(new LocationNotFoundException("Location not found with id: " + id)));
    }

    public static class LocationNotFoundException extends RuntimeException {
        public LocationNotFoundException(String message) {
            super(message);
        }
    }

    public Mono<Ubicacion> saveLocation(Ubicacion location) {
        validateCoordinates(location);
        return setDireccionFromCoordinates(location)
                .flatMap(ubicacionRepository::save);
    }

    public Mono<Ubicacion> updateLocation(ObjectId id, Ubicacion location) {
        validateCoordinates(location);
        return getLocationById(id)
                .flatMap(existingLocation -> setDireccionFromCoordinates(location)
                        .doOnNext(updatedLocation -> {
                            existingLocation.setLatitud(updatedLocation.getLatitud());
                            existingLocation.setLongitud(updatedLocation.getLongitud());
                            existingLocation.setDireccion(updatedLocation.getDireccion());
                        })
                        .flatMap(ubicacionRepository::save));
    }

    public Mono<Void> deleteLocation(ObjectId id) {
        return getLocationById(id)
                .flatMap(_ -> ubicacionRepository.deleteById(id));
    }

    private Mono<Ubicacion> setDireccionFromCoordinates(Ubicacion location) {
        return Mono.fromCallable(() -> geocodingService.getReverseGeocoding(location.getLatitud(), location.getLongitud()))
                .map(direccion -> {
                    location.setDireccion(direccion);
                    return location;
                });
    }

    private void validateCoordinates(Ubicacion location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        if (location.getLatitud() == 0 || location.getLongitud() == 0) {
            throw new IllegalArgumentException("Latitude and longitude cannot be zero");
        }
    }
}
