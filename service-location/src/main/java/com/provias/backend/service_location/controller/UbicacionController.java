package com.provias.backend.service_location.controller;

import com.provias.backend.service_location.dto.ApiResponse;
import com.provias.backend.service_location.model.Ubicacion;
import com.provias.backend.service_location.service.UbicacionService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/locations")
public class UbicacionController {

    private final UbicacionService ubicacionService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<Iterable<Ubicacion>>>> getAllLocations() {
        return ubicacionService.getAllLocations()
                .collectList()
                .map(ubicaciones -> ResponseEntity.ok(
                        ApiResponse.success(ubicaciones, "Ubicaciones recuperadas exitosamente")
                ));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Ubicacion>>> getLocationById(@PathVariable ObjectId id) {
        return ubicacionService.getLocationById(id)
                .map(ubicacion -> ResponseEntity.ok(
                        ApiResponse.success(ubicacion, "Ubicación encontrada exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Ubicación no encontrada")));
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<Ubicacion>>> createLocation(@RequestBody Ubicacion location) {
        return ubicacionService.saveLocation(location)
                .map(savedLocation -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(savedLocation, "Ubicación creada exitosamente")));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Ubicacion>>> updateLocation(
            @PathVariable ObjectId id,
            @RequestBody Ubicacion location) {
        return ubicacionService.updateLocation(id, location)
                .map(updatedLocation -> ResponseEntity.ok(
                        ApiResponse.success(updatedLocation, "Ubicación actualizada exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Ubicación no encontrada")));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deleteLocation(@PathVariable ObjectId id) {
        return ubicacionService.deleteLocation(id)
                .then(Mono.just(ResponseEntity.ok(
                        ApiResponse.<Void>success(null, "Ubicación eliminada exitosamente")
                )))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Void>error("Ubicación no encontrada")));
    }
}
