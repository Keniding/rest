package com.provias.backend.service_category.cotroller;

import com.provias.backend.service_category.model.Categoria;
import com.provias.backend.service_category.service.CategoriaService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/categories")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public Flux<Categoria> getAllCategorias() {
        return categoriaService.getAllCategorias();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Categoria>> getCategoriaById(@PathVariable ObjectId id) {
        return categoriaService.getCategoriaById(id)
                .map(categoria -> new ResponseEntity<>(categoria, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Mono<ResponseEntity<Categoria>> createCategoria(@RequestBody Categoria categoria) {
        return categoriaService.saveCategoria(categoria)
                .map(savedCategoria -> new ResponseEntity<>(savedCategoria, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Categoria>> updateCategoria(@PathVariable ObjectId id, @RequestBody Categoria categoria) {
        return categoriaService.updateCategoria(id, categoria)
                .map(updatedCategoria -> new ResponseEntity<>(updatedCategoria, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCategoria(@PathVariable ObjectId id) {
        return categoriaService.deleteCategoria(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
