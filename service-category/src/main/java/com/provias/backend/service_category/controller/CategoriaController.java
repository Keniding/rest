package com.provias.backend.service_category.controller;

import com.provias.backend.service_category.dto.ApiResponse;
import com.provias.backend.service_category.model.Categoria;
import com.provias.backend.service_category.service.CategoriaService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/categories")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<Iterable<Categoria>>>> getAllCategorias() {
        return categoriaService.getAllCategorias()
                .collectList()
                .map(categorias -> ResponseEntity.ok(
                        ApiResponse.success(categorias, "Categorías recuperadas exitosamente")
                ));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Categoria>>> getCategoriaById(@PathVariable ObjectId id) {
        return categoriaService.getCategoriaById(id)
                .map(categoria -> ResponseEntity.ok(
                        ApiResponse.success(categoria, "Categoría encontrada exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Categoría no encontrada")));
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<Categoria>>> createCategoria(@RequestBody Categoria categoria) {
        return categoriaService.saveCategoria(categoria)
                .map(savedCategoria -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(savedCategoria, "Categoría creada exitosamente")));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Categoria>>> updateCategoria(
            @PathVariable ObjectId id,
            @RequestBody Categoria categoria) {
        return categoriaService.updateCategoria(id, categoria)
                .map(updatedCategoria -> ResponseEntity.ok(
                        ApiResponse.success(updatedCategoria, "Categoría actualizada exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Categoría no encontrada")));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deleteCategoria(@PathVariable ObjectId id) {
        return categoriaService.deleteCategoria(id)
                .then(Mono.just(ResponseEntity.ok(
                        ApiResponse.<Void>success(null, "Categoría eliminada exitosamente")
                )))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Void>error("Categoría no encontrada")));
    }
}
