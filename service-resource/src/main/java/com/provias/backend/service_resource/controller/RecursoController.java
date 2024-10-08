package com.provias.backend.service_resource.controller;

import com.provias.backend.service_resource.model.Recurso;
import com.provias.backend.service_resource.service.RecursoService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/resources")
public class RecursoController {

    private final RecursoService recursoService;

    @GetMapping
    public Flux<Recurso> getAllRecursos() {
        return recursoService.getAllRecursos();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Recurso>> getRecursoById(@PathVariable ObjectId id) {
        return recursoService.getRecursoById(id)
                .map(recurso -> new ResponseEntity<>(recurso, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Mono<ResponseEntity<Recurso>> createRecurso(@RequestBody Recurso recurso) {
        return recursoService.saveRecurso(recurso)
                .map(savedRecurso -> new ResponseEntity<>(savedRecurso, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Recurso>> updateRecurso(@PathVariable ObjectId id, @RequestBody Recurso recurso) {
        return recursoService.updateRecurso(id, recurso)
                .map(updatedRecurso -> new ResponseEntity<>(updatedRecurso, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRecurso(@PathVariable ObjectId id) {
        return recursoService.deleteRecurso(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
