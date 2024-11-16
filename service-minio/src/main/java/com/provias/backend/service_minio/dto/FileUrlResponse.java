package com.provias.backend.service_minio.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class FileUrlResponse {
    private String estado;
    private String url;
    private String nombreArchivo;
    private Map<String, String> metadata;
    private long tamanio;
    private String tipoContenido;
    private String ultimaModificacion;
    private String tiempoExpiracion;
}
