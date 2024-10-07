package com.provias.backend.service_project.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("tipos")
@Data
@NoArgsConstructor
public class Tipo {
    @Id
    private ObjectId id;
    private String nombre;
    private String descripcion;
    private List<String> subcategorias; // Subcategorías para clasificar
}
