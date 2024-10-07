package com.provias.backend.service_project.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("ubicaciones")
@Data
@NoArgsConstructor
public class Ubicacion {
    private double latitud;
    private double longitud;
}