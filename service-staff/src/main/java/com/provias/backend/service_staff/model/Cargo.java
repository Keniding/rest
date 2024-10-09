package com.provias.backend.service_staff.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("cargos")
@Data
@NoArgsConstructor
public class Cargo { // Por ejemplo: "Administrador", "Ingeniero", "Supervisor"
    @Id
    private ObjectId id;
    private String nombre;
    private String descripcion;
    private Rango rango;
    private String departamento; // Por ejemplo: "Ingeniería", "Administración", "Supervisión"
}
