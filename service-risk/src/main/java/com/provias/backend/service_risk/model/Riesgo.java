package com.provias.backend.service_risk.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("riesgos")
@Data
@NoArgsConstructor
public class Riesgo {
    @Id
    private ObjectId id;
    private String nombre;
    private String descripcion;
    private Nivel nivel; // Nivel de riesgo (ej. bajo, medio, alto)
    private String planMitigacion; // Plan de mitigación para el riesgo url
}
