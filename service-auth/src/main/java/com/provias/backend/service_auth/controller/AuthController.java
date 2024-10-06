package com.provias.backend.service_auth.controller;

import com.provias.backend.service_auth.model.AuthRequest;
import com.provias.backend.service_auth.model.AuthResponse;
import com.provias.backend.service_auth.security.TokenUtils;
import com.provias.backend.service_auth.security.UserDetailsImp;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@AllArgsConstructor
public class AuthController {

    private final ReactiveAuthenticationManager authenticationManager;
    private static final Logger LOGGER = Logger.getLogger(AuthRequest.class.getName());

    @PostMapping("/api/auth/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest authRequest) {
        LOGGER.log(Level.INFO, "Attempting to authenticate user: " + authRequest.getUsername());

        return authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()))
                .map(auth -> {
                    LOGGER.log(Level.INFO, "Authentication successful for user: " + auth.getName());
                    String token = TokenUtils.createAccessToken((UserDetailsImp) auth.getPrincipal());
                    return ResponseEntity.ok(new AuthResponse(token));
                })
                .doOnError(e -> LOGGER.log(Level.SEVERE, "Authentication error: " + e.getMessage(), e))
                .onErrorResume(AuthenticationException.class, e -> {
                    LOGGER.log(Level.WARNING, "Authentication failed for user: " + authRequest.getUsername());
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                });
    }

    @PostMapping("/api/auth/validate")
    public Mono<ResponseEntity<Void>> validateToken(@RequestBody String token) {
        LOGGER.log(Level.INFO, "Validating token");

        try {
            boolean isValid = TokenUtils.validateToken(token);
            if (isValid) {
                LOGGER.log(Level.INFO, "Token is valid");
                return Mono.just(ResponseEntity.ok().build());
            } else {
                LOGGER.log(Level.WARNING, "Token is invalid");
                return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Token validation error: " + e.getMessage(), e);
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }
    }

}
