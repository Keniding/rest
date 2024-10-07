package com.provias.backend.service_project.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document("proyectos")
@Data
@NoArgsConstructor
public class Proyecto {
    @Id
    private ObjectId id;
    private String nombre;
    private String descripcion;
    private int cantidadKm;
    private Tipo tipo;
    private List<Recurso> recursos;
    private Date fechaInicio;
    private Date fechaFin;
    private Ubicacion ubicacion;
    private Estado estado;
    private Personal responsable;
    private List<Actividad> actividades;
    private List<Hito> hitos; // Lista de hitos del proyecto
    private List<Riesgo> riesgos; // Lista de riesgos asociados al proyecto
}