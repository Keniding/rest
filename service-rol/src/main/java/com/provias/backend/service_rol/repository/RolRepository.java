package com.provias.backend.service_rol.repository;

import com.provias.backend.service_rol.model.Rol;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface RolRepository extends ReactiveMongoRepository<Rol, ObjectId> {
}
