package com.provias.backend.service_supplier.repository;

import com.provias.backend.service_supplier.model.Proveedor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProveedorRepository extends ReactiveMongoRepository<Proveedor, ObjectId> {
}
