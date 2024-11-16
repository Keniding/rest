package com.provias.backend.service_minio.service;

import com.provias.backend.service_minio.dto.FileUrlResponse;
import com.provias.backend.service_minio.exception.FileStorageException;
import com.provias.backend.service_minio.utils.ContentTypePrefix;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class FileStorageService {

    private final MinioClient minioClient;
    private final String bucketName;

    public FileStorageService(MinioClient minioClient,
                              @Value("${minio.bucket}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        this.initializeBucket();
    }

    private void initializeBucket() {
        try {
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());

            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                log.info("Bucket {} creado exitosamente", bucketName);
            } else {
                log.info("Bucket {} ya existe", bucketName);
            }
        } catch (Exception e) {
            log.error("Error al inicializar el bucket: {}", e.getMessage());
            throw new RuntimeException("Error al inicializar MinIO", e);
        }
    }

    public Mono<Map<String, Object>> uploadFile(FilePart filePart, String objectId) {
        if (!isValidObjectId(objectId)) {
            log.error("ObjectId inválido: {}", objectId);
            return Mono.error(new FileStorageException("ID inválido. Debe ser un ObjectId válido de 24 caracteres hexadecimales."));
        }

        return Mono.from(filePart.content())
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    log.debug("Archivo leído: {} bytes", bytes.length);
                    return Mono.just(bytes);
                })
                .flatMap(bytes -> {
                    try {
                        String fileName = generateFileName(filePart, objectId);
                        String md5Hash = calculateMD5(bytes);

                        return checkDuplicate(md5Hash)
                                .flatMap(existingFileName -> {
                                    if (existingFileName != null) {
                                        log.info("Archivo duplicado encontrado: {}", existingFileName);
                                        return Mono.just(createDuplicateResponse(existingFileName));
                                    }

                                    Map<String, String> userMetadata = new HashMap<>();
                                    userMetadata.put("hash_md5", md5Hash);
                                    userMetadata.put("nombre_original", filePart.filename());
                                    userMetadata.put("fecha_subida", LocalDateTime.now().toString());
                                    userMetadata.put("tamanio", String.valueOf(bytes.length));
                                    userMetadata.put("mime_type", Objects.requireNonNull(filePart.headers().getContentType()).toString());
                                    userMetadata.put("object_id", objectId);

                                    return Mono.fromCallable(() -> {
                                        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                                                .bucket(bucketName)
                                                .object(fileName)
                                                .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                                                .contentType(Objects.requireNonNull(filePart.headers().getContentType()).toString())
                                                .userMetadata(userMetadata)
                                                .build();

                                        minioClient.putObject(putObjectArgs);
                                        log.info("Archivo {} subido exitosamente a MinIO", fileName);
                                        return createSuccessResponse(fileName, userMetadata);
                                    });
                                })
                                .switchIfEmpty(Mono.fromCallable(() -> {
                                    Map<String, String> userMetadata = new HashMap<>();
                                    userMetadata.put("hash_md5", md5Hash);
                                    userMetadata.put("nombre_original", filePart.filename());
                                    userMetadata.put("fecha_subida", LocalDateTime.now().toString());
                                    userMetadata.put("tamanio", String.valueOf(bytes.length));
                                    userMetadata.put("mime_type", Objects.requireNonNull(filePart.headers().getContentType()).toString());
                                    userMetadata.put("object_id", objectId);

                                    PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                                            .bucket(bucketName)
                                            .object(fileName)
                                            .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                                            .contentType(Objects.requireNonNull(filePart.headers().getContentType()).toString())
                                            .userMetadata(userMetadata)
                                            .build();

                                    minioClient.putObject(putObjectArgs);
                                    log.info("Archivo {} subido exitosamente a MinIO", fileName);
                                    return createSuccessResponse(fileName, userMetadata);
                                }));

                    } catch (Exception e) {
                        log.error("Error en el procesamiento del archivo: {}", e.getMessage());
                        return Mono.error(new FileStorageException("No se pudo procesar el archivo", e));
                    }
                })
                .doOnError(e -> {
                    if (e instanceof FileStorageException) {
                        log.error("Error de almacenamiento de archivo: {}", e.getMessage());
                    } else if (e instanceof io.minio.errors.MinioException) {
                        log.error("Error de MinIO: {}", e.getMessage());
                    } else {
                        log.error("Error inesperado: {}", e.getMessage(), e);
                    }
                })
                .onErrorResume(e -> Mono.error(new FileStorageException("Error al subir archivo", e)));
    }

    public Flux<FileUrlResponse> getFilesByObjectId(String objectId) {
        log.info("Buscando archivos para ObjectId: {}", objectId);

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(objectId + "_")
                            .recursive(true)
                            .build());

            log.debug("Búsqueda realizada con prefix: {}", objectId + "_");

            return Flux.fromIterable(results)
                    .flatMap(result -> {
                        try {
                            final Item item = result.get(); // Declaramos item aquí
                            log.debug("Encontrado archivo: {}", item.objectName());

                            StatObjectResponse stat = minioClient.statObject(
                                    StatObjectArgs.builder()
                                            .bucket(bucketName)
                                            .object(item.objectName())
                                            .build());

                            String presignedUrl = minioClient.getPresignedObjectUrl(
                                    GetPresignedObjectUrlArgs.builder()
                                            .bucket(bucketName)
                                            .object(item.objectName())
                                            .method(Method.GET)
                                            .expiry(1, TimeUnit.HOURS)
                                            .build());

                            // Calcular tiempo de expiración (1 hora desde ahora)
                            LocalDateTime expiracion = LocalDateTime.now().plusHours(1);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                            FileUrlResponse fileResponse = FileUrlResponse.builder()
                                    .estado("EXITOSO")
                                    .url(presignedUrl)
                                    .nombreArchivo(item.objectName())
                                    .metadata(stat.userMetadata())
                                    .tamanio(stat.size())
                                    .tipoContenido(stat.contentType())
                                    .ultimaModificacion(stat.lastModified().toInstant()
                                            .atZone(ZoneId.systemDefault())
                                            .format(formatter))
                                    .tiempoExpiracion(expiracion.format(formatter))
                                    .build();

                            log.debug("FileUrlResponse generado: {}", fileResponse);
                            return Mono.just(fileResponse);
                        } catch (Exception e) {
                            log.error("Error al procesar archivo - Error: {}", e.getMessage());
                            return Mono.empty();
                        }
                    })
                    .collectList()
                    .flatMapMany(fileResponses -> {
                        if (fileResponses.isEmpty()) {
                            log.info("No se encontraron archivos para el ObjectId: {}", objectId);
                            return Flux.empty();
                        }
                        log.info("Se encontraron {} archivos para el ObjectId: {}", fileResponses.size(), objectId);
                        return Flux.fromIterable(fileResponses);
                    })
                    .onErrorResume(e -> {
                        log.error("Error al procesar los resultados: {}", e.getMessage());
                        return Flux.empty();
                    });
        } catch (Exception e) {
            log.error("Error al buscar archivos: {}", e.getMessage(), e);
            return Flux.error(new RuntimeException("Error al buscar archivos: " + e.getMessage()));
        }
    }

    public boolean isValidObjectId(String objectId) {
        if (objectId == null || objectId.length() != 24) {
            return false;
        }
        return objectId.matches("[a-fA-F0-9]{24}");
    }

    private String generateFileName(FilePart filePart, String objectId) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = now.format(formatter);
        String originalFileName = filePart.filename();

        int lastDotIndex = originalFileName.lastIndexOf('.');
        String fileNameWithoutExtension;
        String extension;

        if (lastDotIndex > 0) {
            fileNameWithoutExtension = originalFileName.substring(0, lastDotIndex);
            extension = originalFileName.substring(lastDotIndex);
        } else {
            fileNameWithoutExtension = originalFileName;
            extension = "";
        }

        String cleanFileName = fileNameWithoutExtension.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
        cleanFileName = cleanFileName.length() > 20 ? cleanFileName.substring(0, 20) : cleanFileName;

        return objectId + "_" +
                ContentTypePrefix.getPrefix(filePart.headers().getContentType().toString()) +
                timestamp +
                "_" +
                cleanFileName +
                extension;
    }


    private String calculateMD5(byte[] bytes) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(bytes);
        return HexFormat.of().formatHex(md.digest()).toUpperCase();
    }

    private Mono<String> checkDuplicate(String md5Hash) {
        return Flux.defer(() -> {
                    try {
                        Iterable<Result<Item>> results = minioClient.listObjects(
                                ListObjectsArgs.builder()
                                        .bucket(bucketName)
                                        .recursive(true)
                                        .build());
                        return Flux.fromIterable(results);
                    } catch (Exception e) {
                        return Flux.error(new RuntimeException("Error listing objects", e));
                    }
                })
                .flatMap(result -> {
                    try {
                        Item item = result.get();
                        return Mono.fromCallable(() -> {
                            StatObjectResponse stat = minioClient.statObject(
                                    StatObjectArgs.builder()
                                            .bucket(bucketName)
                                            .object(item.objectName())
                                            .build());

                            String storedHash = stat.userMetadata().get("hash_md5");
                            return md5Hash.equals(storedHash) ? item.objectName() : null;
                        });
                    } catch (Exception e) {
                        return Mono.empty();
                    }
                })
                .filter(Objects::nonNull)
                .next();
    }

    private Map<String, Object> createSuccessResponse(String fileName, Map<String, String> metadata) {
        Map<String, Object> response = new HashMap<>();
        response.put("estado", "EXITOSO");
        response.put("nombreArchivo", fileName);
        response.put("message", "Archivo subido exitosamente");

        try {
            String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .method(Method.GET)
                            .expiry(1, TimeUnit.HOURS)
                            .build()
            );
            response.put("url", presignedUrl);
        } catch (Exception e) {
            log.error("Error al generar URL prefirmada para {}: {}", fileName, e.getMessage());
            response.put("url", "");
        }

        response.put("metadata", metadata);
        response.put("tamanio", Long.parseLong(metadata.getOrDefault("tamanio", "0")));
        response.put("tipoContenido", metadata.get("mime_type"));
        response.put("ultimaModificacion", metadata.get("fecha_subida"));

        // Calcular tiempo de expiración (1 hora desde ahora)
        LocalDateTime expiracion = LocalDateTime.now().plusHours(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        response.put("tiempoExpiracion", expiracion.format(formatter));

        return response;
    }

    private Map<String, Object> createDuplicateResponse(String existingFileName) {
        Map<String, Object> response = new HashMap<>();
        response.put("estado", "DUPLICADO");
        response.put("nombreArchivo", existingFileName);
        response.put("message", "Archivo duplicado");

        try {
            // Obtener los metadatos del archivo existente
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(existingFileName)
                            .build());

            String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(existingFileName)
                            .method(Method.GET)
                            .expiry(1, TimeUnit.HOURS)
                            .build()
            );

            response.put("estado", "duplicado");
            response.put("nombreArchivo", existingFileName);
            response.put("url", presignedUrl);
            response.put("metadata", stat.userMetadata());
            response.put("tamanio", stat.size());
            response.put("tipoContenido", stat.contentType());
            response.put("ultimaModificacion", stat.lastModified().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            // Calcular tiempo de expiración (1 hora desde ahora)
            LocalDateTime expiracion = LocalDateTime.now().plusHours(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            response.put("tiempoExpiracion", expiracion.format(formatter));

        } catch (Exception e) {
            log.error("Error al generar respuesta para archivo duplicado {}: {}", existingFileName, e.getMessage());
            response.put("url", "");
            response.put("tamanio", 0L);
            response.put("tipoContenido", "application/octet-stream");
            response.put("ultimaModificacion", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            response.put("tiempoExpiracion", LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            response.put("metadata", new HashMap<String, String>());
        }

        return response;
    }



    public Mono<Void> deleteFile(String fileName, String objectId) {
        return Mono.fromCallable(() ->
                        minioClient.statObject(
                                StatObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(fileName)
                                        .build()
                        ))
                .flatMap(stat -> {
                    String fileObjectId = stat.userMetadata().get("object_id");
                    if (!objectId.equals(fileObjectId)) {
                        return Mono.error(new FileStorageException(
                                "El archivo " + fileName + " no pertenece al objectId: " + objectId
                        ));
                    }

                    return Mono.fromCallable(() -> {
                        minioClient.removeObject(
                                RemoveObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(fileName)
                                        .build()
                        );
                        return null;
                    });
                })
                .then()
                .doOnSuccess(v -> log.info("Archivo eliminado exitosamente: {} del objectId: {}", fileName, objectId))
                .onErrorResume(e -> {
                    log.error("Error al eliminar el archivo: {} del objectId: {}", fileName, objectId, e);
                    return Mono.error(new FileStorageException("No se pudo eliminar el archivo: " + e.getMessage(), e));
                });
    }
}