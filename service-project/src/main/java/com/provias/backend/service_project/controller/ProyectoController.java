package com.provias.backend.service_project.controller;

import com.provias.backend.service_project.dto.ApiResponse;
import com.provias.backend.service_project.model.Proyecto;
import com.provias.backend.service_project.service.ProyectoService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/projects")
public class ProyectoController {

    private final ProyectoService proyectoService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<Iterable<Proyecto>>>> getAllProyectos() {
        return proyectoService.getAllProyectos()
                .collectList()
                .map(proyectos -> ResponseEntity.ok(
                        ApiResponse.success(proyectos, "Proyectos recuperados exitosamente")
                ));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Proyecto>>> getProyectoById(@PathVariable ObjectId id) {
        return proyectoService.getProyectoById(id)
                .map(proyecto -> ResponseEntity.ok(
                        ApiResponse.success(proyecto, "Proyecto encontrado exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Proyecto no encontrado")));
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<Proyecto>>> createProyecto(@RequestBody Proyecto proyecto) {
        return proyectoService.saveProyecto(proyecto)
                .map(savedProyecto -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(savedProyecto, "Proyecto creado exitosamente")));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Proyecto>>> updateProyecto(
            @PathVariable ObjectId id,
            @RequestBody Proyecto proyecto) {
        return proyectoService.updateProyecto(id, proyecto)
                .map(updatedProyecto -> ResponseEntity.ok(
                        ApiResponse.success(updatedProyecto, "Proyecto actualizado exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Proyecto no encontrado")));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deleteProyecto(@PathVariable ObjectId id) {
        return proyectoService.deleteProyecto(id)
                .then(Mono.just(ResponseEntity.ok(
                        ApiResponse.<Void>success(null, "Proyecto eliminado exitosamente")
                )))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Void>error("Proyecto no encontrado")));
    }
}
