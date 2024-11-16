package com.provias.backend.service_staff.controller;

import com.provias.backend.service_staff.model.Personal;
import com.provias.backend.service_staff.service.PersonalService;
import com.provias.backend.service_staff.dto.ApiResponse;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/staff")
public class PersonalController {

    private final PersonalService personalService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<Iterable<Personal>>>> getAllPersonal() {
        return personalService.getAllPersonal()
                .collectList()
                .map(personal -> ResponseEntity.ok(
                        ApiResponse.success(personal, "Personal recuperado exitosamente")
                ));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Personal>>> getPersonalById(@PathVariable ObjectId id) {
        return personalService.getPersonalById(id)
                .map(personal -> ResponseEntity.ok(
                        ApiResponse.success(personal, "Personal encontrado exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Personal no encontrado")));
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<Personal>>> createPersonal(@RequestBody Personal personal) {
        return personalService.savePersonal(personal)
                .map(savedPersonal -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(savedPersonal, "Personal creado exitosamente")));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Personal>>> updatePersonal(
            @PathVariable ObjectId id,
            @RequestBody Personal personal) {
        return personalService.updatePersonal(id, personal)
                .map(updatedPersonal -> ResponseEntity.ok(
                        ApiResponse.success(updatedPersonal, "Personal actualizado exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Personal no encontrado")));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deletePersonal(@PathVariable ObjectId id) {
        return personalService.deletePersonal(id)
                .then(Mono.just(ResponseEntity.ok(
                        ApiResponse.<Void>success(null, "Personal eliminado exitosamente")
                )))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Void>error("Personal no encontrado")));
    }
}
