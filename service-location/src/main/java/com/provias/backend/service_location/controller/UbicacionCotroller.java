package com.provias.backend.service_location.controller;

import com.provias.backend.service_location.model.Ubicacion;
    import com.provias.backend.service_location.service.UbicacionService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/locations")
public class UbicacionCotroller {

    private final UbicacionService ubicacionService;

    @GetMapping
    public Flux<Ubicacion> getAllLocations() {
        return ubicacionService.getAllLocations();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Ubicacion>> getLocationById(@PathVariable ObjectId id) {
        return ubicacionService.getLocationById(id)
                .map(location -> new ResponseEntity<>(location, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Mono<ResponseEntity<Ubicacion>> createLocation(@RequestBody Ubicacion location) {
        return ubicacionService.saveLocation(location)
                .map(savedLocation -> new ResponseEntity<>(savedLocation, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Ubicacion>> updateLocation(@PathVariable ObjectId id, @RequestBody Ubicacion location) {
        return ubicacionService.updateLocation(id, location)
                .map(updatedLocation -> new ResponseEntity<>(updatedLocation, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteLocation(@PathVariable ObjectId id) {
        return ubicacionService.deleteLocation(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
