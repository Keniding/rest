package com.provias.backend.service_rol.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Data
public class Rol {
    @Id
    private ObjectId id;
    private String name;
    private String description;
}
