package com.provias.backend.service_resource.controller;

import com.provias.backend.service_resource.model.Recurso;
import com.provias.backend.service_resource.service.RecursoService;
import com.provias.backend.service_resource.dto.ApiResponse;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/resources")
public class RecursoController {

    private final RecursoService recursoService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<Iterable<Recurso>>>> getAllRecursos() {
        return recursoService.getAllRecursos()
                .collectList()
                .map(recursos -> ResponseEntity.ok(
                        ApiResponse.success(recursos, "Recursos recuperados exitosamente")
                ));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Recurso>>> getRecursoById(@PathVariable ObjectId id) {
        return recursoService.getRecursoById(id)
                .map(recurso -> ResponseEntity.ok(
                        ApiResponse.success(recurso, "Recurso encontrado exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Recurso no encontrado")));
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<Recurso>>> createRecurso(@RequestBody Recurso recurso) {
        return recursoService.saveRecurso(recurso)
                .map(savedRecurso -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(savedRecurso, "Recurso creado exitosamente")));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Recurso>>> updateRecurso(
            @PathVariable ObjectId id,
            @RequestBody Recurso recurso) {
        return recursoService.updateRecurso(id, recurso)
                .map(updatedRecurso -> ResponseEntity.ok(
                        ApiResponse.success(updatedRecurso, "Recurso actualizado exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Recurso no encontrado")));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deleteRecurso(@PathVariable ObjectId id) {
        return recursoService.deleteRecurso(id)
                .then(Mono.just(ResponseEntity.ok(
                        ApiResponse.<Void>success(null, "Recurso eliminado exitosamente")
                )))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Void>error("Recurso no encontrado")));
    }
}
