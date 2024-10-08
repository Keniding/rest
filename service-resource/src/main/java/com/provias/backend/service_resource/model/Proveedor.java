package com.provias.backend.service_resource.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("proveedores")
@Data
@NoArgsConstructor
public class Proveedor {
    @Id
    private ObjectId id;
    private String nombre;
    private String contacto;
    private String telefono;
    private String correo;
}
