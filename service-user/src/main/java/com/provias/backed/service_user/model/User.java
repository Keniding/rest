package com.provias.backed.service_user.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Builder
@Data
public class User {
    @Id
    private UUID id;
    private String username;
    private String password;
    private Rol rol;
}
