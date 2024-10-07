package com.provias.backend.service_project.controller;

import com.provias.backend.service_project.model.Proyecto;
import com.provias.backend.service_project.service.ProyectoService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/projects")
public class ProyectoController {

    private final ProyectoService proyectoService;

    @GetMapping
    public Flux<Proyecto> getAllProyectos() {
        return proyectoService.getAllProyectos();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Proyecto>> getProyectoById(@PathVariable ObjectId id) {
        return proyectoService.getProyectoById(id)
                .map(proyecto -> new ResponseEntity<>(proyecto, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Mono<ResponseEntity<Proyecto>> createProyecto(@RequestBody Proyecto proyecto) {
        return proyectoService.saveProyecto(proyecto)
                .map(savedProyecto -> new ResponseEntity<>(savedProyecto, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Proyecto>> updateProyecto(@PathVariable ObjectId id, @RequestBody Proyecto proyecto) {
        return proyectoService.updateProyecto(id, proyecto)
                .map(updatedProyecto -> new ResponseEntity<>(updatedProyecto, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProyecto(@PathVariable ObjectId id) {
        return proyectoService.deleteProyecto(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
