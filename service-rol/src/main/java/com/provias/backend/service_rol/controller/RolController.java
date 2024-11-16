package com.provias.backend.service_rol.controller;

import com.provias.backend.service_rol.model.Rol;
import com.provias.backend.service_rol.service.RolService;
import com.provias.backend.service_rol.dto.ApiResponse;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<Iterable<Rol>>>> getAllRoles() {
        return rolService.getAllRoles()
                .collectList()
                .map(roles -> ResponseEntity.ok(
                        ApiResponse.success(roles, "Roles recuperados exitosamente")
                ));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Rol>>> getRolById(@PathVariable ObjectId id) {
        return rolService.getRolById(id)
                .map(rol -> ResponseEntity.ok(
                        ApiResponse.success(rol, "Rol encontrado exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Rol no encontrado")));
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<Rol>>> createRol(@RequestBody Rol rol) {
        return rolService.createRol(rol)
                .map(savedRol -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(savedRol, "Rol creado exitosamente")));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Rol>>> updateRol(
            @PathVariable ObjectId id,
            @RequestBody Rol rolDetails) {
        return rolService.updateRol(id, rolDetails)
                .map(updatedRol -> ResponseEntity.ok(
                        ApiResponse.success(updatedRol, "Rol actualizado exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Rol no encontrado")));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deleteRol(@PathVariable ObjectId id) {
        return rolService.deleteRol(id)
                .then(Mono.just(ResponseEntity.ok(
                        ApiResponse.<Void>success(null, "Rol eliminado exitosamente")
                )))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Void>error("Rol no encontrado")));
    }
}
