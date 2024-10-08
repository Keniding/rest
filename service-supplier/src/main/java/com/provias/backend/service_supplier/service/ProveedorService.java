package com.provias.backend.service_supplier.service;

import com.provias.backend.service_supplier.model.Proveedor;
import com.provias.backend.service_supplier.repository.ProveedorRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public Flux<Proveedor> getAllProveedores() {
        return proveedorRepository.findAll();
    }

    public Mono<Proveedor> getProveedorById(ObjectId id) {
        return proveedorRepository.findById(id)
                .switchIfEmpty(Mono.error(new ProveedorNotFoundException("Proveedor no encontrado con id: " + id)));
    }

    public Mono<Proveedor> saveProveedor(Proveedor proveedor) {
        validateProveedor(proveedor);
        return proveedorRepository.save(proveedor);
    }

    public Mono<Proveedor> updateProveedor(ObjectId id, Proveedor proveedor) {
        return getProveedorById(id)
                .flatMap(existingProveedor -> {
                    validateProveedor(proveedor);
                    existingProveedor.setNombre(proveedor.getNombre());
                    existingProveedor.setContacto(proveedor.getContacto());
                    existingProveedor.setTelefono(proveedor.getTelefono());
                    existingProveedor.setCorreo(proveedor.getCorreo());

                    return proveedorRepository.save(existingProveedor);
                });
    }

    public Mono<Void> deleteProveedor(ObjectId id) {
        return validateProveedorExists(id)
                .flatMap(_ -> proveedorRepository.deleteById(id));
    }

    private Mono<Proveedor> validateProveedorExists(ObjectId id) {
        return proveedorRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new ProveedorNotFoundException("Proveedor no encontrado con id: " + id));
                    }
                    return proveedorRepository.findById(id);
                });
    }

    private void validateProveedor(Proveedor proveedor) {
        if (proveedor == null) {
            throw new IllegalArgumentException("El proveedor no puede ser nulo");
        }
        if (proveedor.getNombre() == null || proveedor.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proveedor no puede ser nulo o vacío");
        }
        if (proveedor.getCorreo() == null || proveedor.getCorreo().isEmpty()) {
            throw new IllegalArgumentException("El correo del proveedor no puede ser nulo o vacío");
        }
    }

    public static class ProveedorNotFoundException extends RuntimeException {
        public ProveedorNotFoundException(String message) {
            super(message);
        }
    }
}
