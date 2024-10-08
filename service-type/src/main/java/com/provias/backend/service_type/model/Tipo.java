package com.provias.backend.service_type.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("tipos")
@Data
public class Tipo {
    @Id
    private ObjectId id;
    private String nombre;
    private String descripcion;
    private List<Categoria> subcategorias;
}