package com.provias.backend.service_user.controller;

import com.provias.backend.service_user.model.Rol;
import com.provias.backend.service_user.model.User;
import com.provias.backend.service_user.service.UserService;
import com.provias.backend.service_user.dto.ApiResponse;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<Iterable<User>>>> getAllUsers() {
        return userService.getAllUsers()
                .collectList()
                .map(users -> ResponseEntity.ok(
                        ApiResponse.success(users, "Usuarios recuperados exitosamente")
                ));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<User>>> getUserById(@PathVariable ObjectId id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(
                        ApiResponse.success(user, "Usuario encontrado exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Usuario no encontrado")));
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<User>>> createUser(@RequestBody User user) {
        return userService.saveUser(user)
                .map(savedUser -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(savedUser, "Usuario creado exitosamente")));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<User>>> updateUser(
            @PathVariable ObjectId id,
            @RequestBody User user) {
        return userService.updateUser(id, user)
                .map(updatedUser -> ResponseEntity.ok(
                        ApiResponse.success(updatedUser, "Usuario actualizado exitosamente")
                ))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Usuario no encontrado")));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deleteUser(@PathVariable ObjectId id) {
        return userService.deleteUser(id)
                .then(Mono.just(ResponseEntity.ok(
                        ApiResponse.<Void>success(null, "Usuario eliminado exitosamente")
                )))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Void>error("Usuario no encontrado")));
    }

    @PutMapping("/{id}/role")
    public Mono<ResponseEntity<ApiResponse<Void>>> updateUserRole(
            @PathVariable ObjectId id,
            @RequestBody Rol role) {
        return userService.updateUserRole(id, role)
                .then(Mono.just(ResponseEntity.ok(
                        ApiResponse.<Void>success(null, "Rol de usuario actualizado exitosamente")
                )))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Void>error("Usuario no encontrado")));
    }
}
