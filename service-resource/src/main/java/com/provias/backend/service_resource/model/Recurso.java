package com.provias.backend.service_resource.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document("recursos")
@Data
@NoArgsConstructor
public class Recurso {
    @Id
    private ObjectId id;
    private String nombre;
    private String descripcion;
    private double costo;
    private String unidad; // Unidad de medida (ej. horas, piezas)
    private int cantidadDisponible;
    private Date fechaAdquisicion;
    private List<Proveedor> proveedor;
}
