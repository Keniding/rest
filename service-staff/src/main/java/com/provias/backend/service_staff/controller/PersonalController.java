package com.provias.backend.service_staff.controller;

import com.provias.backend.service_staff.model.Personal;
import com.provias.backend.service_staff.service.PersonalService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/staff")
public class PersonalController {

    private final PersonalService personalService;

    @GetMapping
    public Flux<Personal> getAllPersonal() {
        return personalService.getAllPersonal();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Personal>> getPersonalById(@PathVariable ObjectId id) {
        return personalService.getPersonalById(id)
                .map(personal -> new ResponseEntity<>(personal, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Mono<ResponseEntity<Personal>> createPersonal(@RequestBody Personal personal) {
        return personalService.savePersonal(personal)
                .map(savedPersonal -> new ResponseEntity<>(savedPersonal, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Personal>> updatePersonal(@PathVariable ObjectId id, @RequestBody Personal personal) {
        return personalService.updatePersonal(id, personal)
                .map(updatedPersonal -> new ResponseEntity<>(updatedPersonal, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePersonal(@PathVariable ObjectId id) {
        return personalService.deletePersonal(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
