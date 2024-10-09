package com.provias.backend.service_milestone.controller;

import com.provias.backend.service_milestone.model.Hito;
import com.provias.backend.service_milestone.service.HitoService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/milestones")
public class HitoController {

    private final HitoService hitoService;

    @GetMapping
    public Flux<Hito> getAllHitos() {
        return hitoService.getAllHitos();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Hito>> getHitoById(@PathVariable ObjectId id) {
        return hitoService.getHitoById(id)
                .map(hito -> new ResponseEntity<>(hito, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Mono<ResponseEntity<Hito>> createHito(@RequestBody Hito hito) {
        return hitoService.saveHito(hito)
                .map(savedHito -> new ResponseEntity<>(savedHito, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Hito>> updateHito(@PathVariable ObjectId id, @RequestBody Hito hito) {
        return hitoService.updateHito(id, hito)
                .map(updatedHito -> new ResponseEntity<>(updatedHito, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteHito(@PathVariable ObjectId id) {
        return hitoService.deleteHito(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
