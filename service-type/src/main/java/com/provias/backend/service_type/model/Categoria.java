package com.provias.backend.service_type.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document("categorias")
public class Categoria {
    @Id
    private ObjectId id;
    private String nombre;
    private String descripcion;
}
