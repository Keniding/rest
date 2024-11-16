package com.provias.backend.service_type.controller;

import com.provias.backend.service_type.model.Tipo;
import com.provias.backend.service_type.service.TipoService;
import com.provias.backend.service_type.dto.ApiResponse;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/types")
public class TipoController {

    private final TipoService tipoService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<Iterable<Tipo>>>> getAllTipos() {
        return tipoService.getAllTipos()
                .collectList()
                .map(tipos -> ResponseEntity.ok(
                        ApiResponse.success(tipos, "Tipos recuperados exitosamente")
                ));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Tipo>>> getTipoById(@PathVariable ObjectId id) {
        return tipoService.getTipoById(id)
                .map(tipo -> ResponseEntity.ok(
                        ApiResponse.success(tipo, "Tipo encontrado exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Tipo no encontrado")));
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<Tipo>>> createTipo(@RequestBody Tipo tipo) {
        return tipoService.saveTipo(tipo)
                .map(savedTipo -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(savedTipo, "Tipo creado exitosamente")));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Tipo>>> updateTipo(
            @PathVariable ObjectId id,
            @RequestBody Tipo tipo) {
        return tipoService.updateTipo(id, tipo)
                .map(updatedTipo -> ResponseEntity.ok(
                        ApiResponse.success(updatedTipo, "Tipo actualizado exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Tipo no encontrado")));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deleteTipo(@PathVariable ObjectId id) {
        return tipoService.deleteTipo(id)
                .then(Mono.just(ResponseEntity.ok(
                        ApiResponse.<Void>success(null, "Tipo eliminado exitosamente")
                )))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Void>error("Tipo no encontrado")));
    }
}
