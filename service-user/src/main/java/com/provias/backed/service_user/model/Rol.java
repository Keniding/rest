package com.provias.backed.service_user.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Data
public class Rol {
    @Id
    private UUID id;
    private String name;
    private String description;
}
