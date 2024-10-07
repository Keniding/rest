package com.provias.backend.service_project.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("actividades")
@Data
@NoArgsConstructor
public class Actividad {
    @Id
    private ObjectId id;
    private String nombre;
    private String descripcion;
    private Date fechaInicio;
    private Date fechaFin;
    private Estado estado; // Estado de la actividad (ej. pendiente, en progreso, completada)
}
