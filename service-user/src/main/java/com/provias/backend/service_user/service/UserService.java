package com.provias.backend.service_user.service;

import com.provias.backend.service_user.model.Rol;
import com.provias.backend.service_user.model.User;
import com.provias.backend.service_user.repository.RolRepository;
import com.provias.backend.service_user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RolRepository rolRepository;

    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Mono<User> getUserById(ObjectId id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found with id: " + id)));
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public Mono<User> saveUser(User user) {
        validateUser(user);
        return rolRepository.findById(user.getRolId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Rol not found with id: " + user.getRolId())))
                .then(userRepository.save(user));
    }

    public Mono<User> updateUser(ObjectId id, User user) {
        return getUserById(id)
                .flatMap(existingUser -> {
                    validateUser(user);
                    existingUser.setUsername(user.getUsername());
                    existingUser.setPassword(encryptPassword(user.getPassword()));
                    existingUser.setRolId(user.getRolId());
                    return rolRepository.findById(user.getRolId())
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("Rol not found with id: " + user.getRolId())))
                            .then(userRepository.save(existingUser));
                });
    }

    public Mono<Void> deleteUser(ObjectId id) {
        return validateUserExists(id)
                .flatMap(_ -> userRepository.deleteById(id));
    }

    public Mono<User> updateUserRole(ObjectId id, Rol rol) {
        return getUserById(id)
                .flatMap(user -> {
                    user.setRol(rol);
                    return userRepository.save(user);
                });
    }

    private Mono<User> validateUserExists(ObjectId id) {
        return userRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new UserNotFoundException("User not found with id: " + id));
                    }
                    return userRepository.findById(id);
                });
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
    }

    private String encryptPassword(String password) {
        return password;
    }
}
