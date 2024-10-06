package com.provias.backed.service_user.service;

import com.provias.backed.service_user.model.Rol;
import com.provias.backed.service_user.model.User;
import com.provias.backed.service_user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> users() {
        return userRepository.findAll();
    }

    public User user(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    public void updateUser(UUID id, User user) {
        validateUserExists(id);
        userRepository.save(
                User.builder()
                        .id(id)
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .build());
    }

    public void deleteUser(UUID id) {
        validateUserExists(id);
        userRepository.deleteById(id);
    }

    public void updateRolUser(UUID id, Rol rol) {
        validateUserExists(id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRol(rol);
        userRepository.save(user);
    }

    private void validateUserExists(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
    }
}