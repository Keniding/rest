package com.provias.backend.service_staff.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("personal")
@Data
@NoArgsConstructor
public class Personal {
    @Id
    private ObjectId id;
    private String nombre;
    private Cargo cargo;
    private String telefono;
    private String correo;
}