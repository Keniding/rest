package com.provias.backend.service_auth.model;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
