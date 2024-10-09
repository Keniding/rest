package com.provias.backend.service_risk.controller;

import com.provias.backend.service_risk.model.Riesgo;
import com.provias.backend.service_risk.service.RiesgoService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/risks")
public class RiesgoController {

    private final RiesgoService riesgoService;

    @GetMapping
    public Flux<Riesgo> getAllRiesgos() {
        return riesgoService.getAllRiesgos();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Riesgo>> getRiesgoById(@PathVariable ObjectId id) {
        return riesgoService.getRiesgoById(id)
                .map(riesgo -> new ResponseEntity<>(riesgo, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Mono<ResponseEntity<Riesgo>> createRiesgo(@RequestBody Riesgo riesgo) {
        return riesgoService.saveRiesgo(riesgo)
                .map(savedRiesgo -> new ResponseEntity<>(savedRiesgo, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Riesgo>> updateRiesgo(@PathVariable ObjectId id, @RequestBody Riesgo riesgo) {
        return riesgoService.updateRiesgo(id, riesgo)
                .map(updatedRiesgo -> new ResponseEntity<>(updatedRiesgo, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRiesgo(@PathVariable ObjectId id) {
        return riesgoService.deleteRiesgo(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
