package com.provias.backend.service_activity.controller;

import com.provias.backend.service_activity.model.Actividad;
import com.provias.backend.service_activity.service.ActividadService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/activities")
public class ActividadController {

    private final ActividadService actividadService;

    @GetMapping
    public Flux<Actividad> getAllActividades() {
        return actividadService.getAllActividades();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Actividad>> getActividadById(@PathVariable ObjectId id) {
        return actividadService.getActividadById(id)
                .map(actividad -> new ResponseEntity<>(actividad, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Mono<ResponseEntity<Actividad>> createActividad(@RequestBody Actividad actividad) {
        return actividadService.saveActividad(actividad)
                .map(savedActividad -> new ResponseEntity<>(savedActividad, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Actividad>> updateActividad(@PathVariable ObjectId id, @RequestBody Actividad actividad) {
        return actividadService.updateActividad(id, actividad)
                .map(updatedActividad -> new ResponseEntity<>(updatedActividad, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteActividad(@PathVariable ObjectId id) {
        return actividadService.deleteActividad(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
