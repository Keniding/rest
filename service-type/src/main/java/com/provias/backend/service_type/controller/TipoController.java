package com.provias.backend.service_type.controller;

import com.provias.backend.service_type.model.Tipo;
import com.provias.backend.service_type.service.TipoService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/types")
public class TipoController {

    private final TipoService tipoService;

    @GetMapping
    public Flux<Tipo> getAllTipos() {
        return tipoService.getAllTipos();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Tipo>> getTipoById(@PathVariable ObjectId id) {
        return tipoService.getTipoById(id)
                .map(tipo -> new ResponseEntity<>(tipo, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Mono<ResponseEntity<Tipo>> createTipo(@RequestBody Tipo tipo) {
        return tipoService.saveTipo(tipo)
                .map(savedTipo -> new ResponseEntity<>(savedTipo, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Tipo>> updateTipo(@PathVariable ObjectId id, @RequestBody Tipo tipo) {
        return tipoService.updateTipo(id, tipo)
                .map(updatedTipo -> new ResponseEntity<>(updatedTipo, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTipo(@PathVariable ObjectId id) {
        return tipoService.deleteTipo(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
