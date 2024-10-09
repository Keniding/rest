package com.provias.backend.service_post.controller;

import com.provias.backend.service_post.model.Cargo;
import com.provias.backend.service_post.service.CargoService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/posts")
public class CargoController {

    private final CargoService cargoService;

    @GetMapping
    public Flux<Cargo> getAllCargos() {
        return cargoService.getAllCargos();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Cargo>> getCargoById(@PathVariable ObjectId id) {
        return cargoService.getCargoById(id)
                .map(cargo -> new ResponseEntity<>(cargo, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Mono<ResponseEntity<Cargo>> createCargo(@RequestBody Cargo cargo) {
        return cargoService.saveCargo(cargo)
                .map(savedCargo -> new ResponseEntity<>(savedCargo, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Cargo>> updateCargo(@PathVariable ObjectId id, @RequestBody Cargo cargo) {
        return cargoService.updateCargo(id, cargo)
                .map(updatedCargo -> new ResponseEntity<>(updatedCargo, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCargo(@PathVariable ObjectId id) {
        return cargoService.deleteCargo(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
