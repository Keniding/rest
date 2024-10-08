package com.provias.backend.service_supplier.controller;

import com.provias.backend.service_supplier.model.Proveedor;
import com.provias.backend.service_supplier.service.ProveedorService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/suppliers")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    public Flux<Proveedor> getAllProveedores() {
        return proveedorService.getAllProveedores();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Proveedor>> getProveedorById(@PathVariable ObjectId id) {
        return proveedorService.getProveedorById(id)
                .map(proveedor -> new ResponseEntity<>(proveedor, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Mono<ResponseEntity<Proveedor>> createProveedor(@RequestBody Proveedor proveedor) {
        return proveedorService.saveProveedor(proveedor)
                .map(savedProveedor -> new ResponseEntity<>(savedProveedor, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Proveedor>> updateProveedor(@PathVariable ObjectId id, @RequestBody Proveedor proveedor) {
        return proveedorService.updateProveedor(id, proveedor)
                .map(updatedProveedor -> new ResponseEntity<>(updatedProveedor, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProveedor(@PathVariable ObjectId id) {
        return proveedorService.deleteProveedor(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
