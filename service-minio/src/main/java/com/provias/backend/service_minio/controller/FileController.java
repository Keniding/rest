package com.provias.backend.service_minio.controller;

import com.provias.backend.service_minio.dto.ApiResponse;
import com.provias.backend.service_minio.dto.FileUrlResponse;
import com.provias.backend.service_minio.exception.FileStorageException;
import com.provias.backend.service_minio.service.FileStorageService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private final FileStorageService fileStorageService;

    @PostMapping(value = "/upload/{objectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ApiResponse<FileUrlResponse>>> uploadFile(
            @RequestPart("file") Mono<FilePart> filePart,
            @PathVariable String objectId) {
        return filePart
                .flatMap(file -> fileStorageService.uploadFile(file, objectId))
                .map(response -> ResponseEntity.ok(ApiResponse.success(
                        FileUrlResponse.builder()
                                .estado((String) response.get("estado"))
                                .url((String) response.get("url"))
                                .nombreArchivo((String) response.get("nombreArchivo"))
                                .metadata((Map<String, String>) response.getOrDefault("metadata", new HashMap<>()))
                                .tamanio((Long) response.get("tamanio"))
                                .tipoContenido((String) response.get("tipoContenido"))
                                .ultimaModificacion((String) response.get("ultimaModificacion"))
                                .tiempoExpiracion((String) response.get("tiempoExpiracion"))
                                .build(),
                        response.get("message").toString()
                )))
                .onErrorResume(FileStorageException.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error(e.getMessage()))));
    }

    @GetMapping("/object/{objectId}")
    public Mono<ResponseEntity<ApiResponse<List<FileUrlResponse>>>> getFilesByObjectId(@PathVariable String objectId) {
        if (!fileStorageService.isValidObjectId(objectId)) {
            return Mono.just(ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("ID inválido. Debe ser un ObjectId válido de 24 caracteres hexadecimales")));
        }

        return fileStorageService.getFilesByObjectId(objectId)
                .collectList()
                .map(responses -> {
                    if (responses.isEmpty()) {
                        return ResponseEntity.ok(ApiResponse.success(
                                responses,
                                "No se encontraron archivos para el ObjectId proporcionado"
                        ));
                    }

                    boolean hasDuplicates = responses.stream()
                            .anyMatch(file -> "duplicado".equals(file.getEstado()));

                    if (hasDuplicates) {
                        return ResponseEntity.ok(ApiResponse.success(
                                responses,
                                "Se encontraron archivos duplicados"
                        ));
                    }

                    return ResponseEntity.ok(ApiResponse.success(
                            responses,
                            "Archivos recuperados exitosamente"
                    ));
                })
                .onErrorResume(FileStorageException.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error(e.getMessage()))))
                .onErrorResume(Exception.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error("Error al recuperar los archivos: " + e.getMessage()))));
    }

    @DeleteMapping("/{objectId}/{fileName}")
    public Mono<ResponseEntity<ApiResponse<Object>>> deleteFile(
            @PathVariable String objectId,
            @PathVariable String fileName) {
        return fileStorageService.deleteFile(fileName, objectId)
                .then(Mono.just(ResponseEntity.ok(
                        ApiResponse.success(null, "Archivo eliminado correctamente")
                )))
                .onErrorResume(FileStorageException.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error(e.getMessage()))));
    }
}
