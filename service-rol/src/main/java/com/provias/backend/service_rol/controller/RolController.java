package com.provias.backend.service_rol.controller;

import com.provias.backend.service_rol.model.Rol;
import com.provias.backend.service_rol.service.RolService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping
    public Flux<Rol> getAllRoles() {
        return rolService.getAllRoles();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Rol>> getRolById(@PathVariable ObjectId id) {
        return rolService.getRolById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<Rol> createRol(@RequestBody Rol rol) {
        return rolService.createRol(rol);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Rol>> updateRol(@PathVariable ObjectId id, @RequestBody Rol rolDetails) {
        return rolService.updateRol(id, rolDetails)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRol(@PathVariable ObjectId id) {
        return rolService.deleteRol(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
