package com.provias.backend.service_rol.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "roles")
public class Rol {
    @Id
    private ObjectId id;
    private String name;
    private String description;
    private List<String> permisos;
}
