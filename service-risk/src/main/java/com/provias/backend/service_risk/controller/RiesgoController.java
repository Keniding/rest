package com.provias.backend.service_risk.controller;

import com.provias.backend.service_risk.model.Riesgo;
import com.provias.backend.service_risk.service.RiesgoService;
import com.provias.backend.service_risk.dto.ApiResponse;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/risks")
public class RiesgoController {

    private final RiesgoService riesgoService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<Iterable<Riesgo>>>> getAllRiesgos() {
        return riesgoService.getAllRiesgos()
                .collectList()
                .map(riesgos -> ResponseEntity.ok(
                        ApiResponse.success(riesgos, "Riesgos recuperados exitosamente")
                ));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Riesgo>>> getRiesgoById(@PathVariable ObjectId id) {
        return riesgoService.getRiesgoById(id)
                .map(riesgo -> ResponseEntity.ok(
                        ApiResponse.success(riesgo, "Riesgo encontrado exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Riesgo no encontrado")));
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<Riesgo>>> createRiesgo(@RequestBody Riesgo riesgo) {
        return riesgoService.saveRiesgo(riesgo)
                .map(savedRiesgo -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(savedRiesgo, "Riesgo creado exitosamente")));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Riesgo>>> updateRiesgo(
            @PathVariable ObjectId id,
            @RequestBody Riesgo riesgo) {
        return riesgoService.updateRiesgo(id, riesgo)
                .map(updatedRiesgo -> ResponseEntity.ok(
                        ApiResponse.success(updatedRiesgo, "Riesgo actualizado exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Riesgo no encontrado")));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deleteRiesgo(@PathVariable ObjectId id) {
        return riesgoService.deleteRiesgo(id)
                .then(Mono.just(ResponseEntity.ok(
                        ApiResponse.<Void>success(null, "Riesgo eliminado exitosamente")
                )))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Void>error("Riesgo no encontrado")));
    }
}
