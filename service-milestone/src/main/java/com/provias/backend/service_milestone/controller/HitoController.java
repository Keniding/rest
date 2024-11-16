package com.provias.backend.service_milestone.controller;

import com.provias.backend.service_milestone.dto.ApiResponse;
import com.provias.backend.service_milestone.model.Hito;
import com.provias.backend.service_milestone.service.HitoService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/milestones")
public class HitoController {

    private final HitoService hitoService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<Iterable<Hito>>>> getAllHitos() {
        return hitoService.getAllHitos()
                .collectList()
                .map(hitos -> ResponseEntity.ok(
                        ApiResponse.success(hitos, "Hitos recuperados exitosamente")
                ));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Hito>>> getHitoById(@PathVariable ObjectId id) {
        return hitoService.getHitoById(id)
                .map(hito -> ResponseEntity.ok(
                        ApiResponse.success(hito, "Hito encontrado exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Hito no encontrado")));
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<Hito>>> createHito(@RequestBody Hito hito) {
        return hitoService.saveHito(hito)
                .map(savedHito -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(savedHito, "Hito creado exitosamente")));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Hito>>> updateHito(
            @PathVariable ObjectId id,
            @RequestBody Hito hito) {
        return hitoService.updateHito(id, hito)
                .map(updatedHito -> ResponseEntity.ok(
                        ApiResponse.success(updatedHito, "Hito actualizado exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Hito no encontrado")));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deleteHito(@PathVariable ObjectId id) {
        return hitoService.deleteHito(id)
                .then(Mono.just(ResponseEntity.ok(
                        ApiResponse.<Void>success(null, "Hito eliminado exitosamente")
                )))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Void>error("Hito no encontrado")));
    }
}
