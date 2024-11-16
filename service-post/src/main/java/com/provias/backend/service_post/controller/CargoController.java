package com.provias.backend.service_post.controller;

import com.provias.backend.service_post.dto.ApiResponse;
import com.provias.backend.service_post.model.Cargo;
import com.provias.backend.service_post.service.CargoService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/posts")
public class CargoController {

    private final CargoService cargoService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<Iterable<Cargo>>>> getAllCargos() {
        return cargoService.getAllCargos()
                .collectList()
                .map(cargos -> ResponseEntity.ok(
                        ApiResponse.success(cargos, "Cargos recuperados exitosamente")
                ));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Cargo>>> getCargoById(@PathVariable ObjectId id) {
        return cargoService.getCargoById(id)
                .map(cargo -> ResponseEntity.ok(
                        ApiResponse.success(cargo, "Cargo encontrado exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Cargo no encontrado")));
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<Cargo>>> createCargo(@RequestBody Cargo cargo) {
        return cargoService.saveCargo(cargo)
                .map(savedCargo -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(savedCargo, "Cargo creado exitosamente")));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Cargo>>> updateCargo(
            @PathVariable ObjectId id,
            @RequestBody Cargo cargo) {
        return cargoService.updateCargo(id, cargo)
                .map(updatedCargo -> ResponseEntity.ok(
                        ApiResponse.success(updatedCargo, "Cargo actualizado exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Cargo no encontrado")));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deleteCargo(@PathVariable ObjectId id) {
        return cargoService.deleteCargo(id)
                .then(Mono.just(ResponseEntity.ok(
                        ApiResponse.<Void>success(null, "Cargo eliminado exitosamente")
                )))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Void>error("Cargo no encontrado")));
    }
}
