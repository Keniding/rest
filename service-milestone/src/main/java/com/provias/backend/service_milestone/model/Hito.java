package com.provias.backend.service_milestone.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("hitos")
@Data
@NoArgsConstructor
public class Hito {
    @Id
    private ObjectId id;
    private String nombre;
    private String descripcion;
    private Date fecha;
}
