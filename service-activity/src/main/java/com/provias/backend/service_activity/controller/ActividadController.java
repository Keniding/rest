package com.provias.backend.service_activity.controller;

import com.provias.backend.service_activity.dto.ApiResponse;
import com.provias.backend.service_activity.model.Actividad;
import com.provias.backend.service_activity.service.ActividadService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/activities")
public class ActividadController {

    private final ActividadService actividadService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<Iterable<Actividad>>>> getAllActividades() {
        return actividadService.getAllActividades()
                .collectList()
                .map(actividades -> ResponseEntity.ok(
                        ApiResponse.success(actividades, "Actividades recuperadas exitosamente")
                ));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Actividad>>> getActividadById(@PathVariable ObjectId id) {
        return actividadService.getActividadById(id)
                .map(actividad -> ResponseEntity.ok(
                        ApiResponse.success(actividad, "Actividad encontrada exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Actividad no encontrada")));
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<Actividad>>> createActividad(@RequestBody Actividad actividad) {
        return actividadService.saveActividad(actividad)
                .map(savedActividad -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(savedActividad, "Actividad creada exitosamente")));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Actividad>>> updateActividad(
            @PathVariable ObjectId id,
            @RequestBody Actividad actividad) {
        return actividadService.updateActividad(id, actividad)
                .map(updatedActividad -> ResponseEntity.ok(
                        ApiResponse.success(updatedActividad, "Actividad actualizada exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Actividad no encontrada")));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deleteActividad(@PathVariable ObjectId id) {
        return actividadService.deleteActividad(id)
                .then(Mono.just(ResponseEntity.ok(
                        ApiResponse.<Void>success(null, "Actividad eliminada exitosamente")
                )))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Void>error("Actividad no encontrada")));
    }
}
