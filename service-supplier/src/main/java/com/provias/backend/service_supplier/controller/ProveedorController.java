package com.provias.backend.service_supplier.controller;

import com.provias.backend.service_supplier.model.Proveedor;
import com.provias.backend.service_supplier.service.ProveedorService;
import com.provias.backend.service_supplier.dto.ApiResponse;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/suppliers")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<Iterable<Proveedor>>>> getAllProveedores() {
        return proveedorService.getAllProveedores()
                .collectList()
                .map(proveedores -> ResponseEntity.ok(
                        ApiResponse.success(proveedores, "Proveedores recuperados exitosamente")
                ));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Proveedor>>> getProveedorById(@PathVariable ObjectId id) {
        return proveedorService.getProveedorById(id)
                .map(proveedor -> ResponseEntity.ok(
                        ApiResponse.success(proveedor, "Proveedor encontrado exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Proveedor no encontrado")));
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<Proveedor>>> createProveedor(@RequestBody Proveedor proveedor) {
        return proveedorService.saveProveedor(proveedor)
                .map(savedProveedor -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(savedProveedor, "Proveedor creado exitosamente")));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Proveedor>>> updateProveedor(
            @PathVariable ObjectId id,
            @RequestBody Proveedor proveedor) {
        return proveedorService.updateProveedor(id, proveedor)
                .map(updatedProveedor -> ResponseEntity.ok(
                        ApiResponse.success(updatedProveedor, "Proveedor actualizado exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Proveedor no encontrado")));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deleteProveedor(@PathVariable ObjectId id) {
        return proveedorService.deleteProveedor(id)
                .then(Mono.just(ResponseEntity.ok(
                        ApiResponse.<Void>success(null, "Proveedor eliminado exitosamente")
                )))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Void>error("Proveedor no encontrado")));
    }
}
