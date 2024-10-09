package com.provias.backend.service_location.repository;

import com.provias.backend.service_location.model.Ubicacion;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UbicacionRepository extends ReactiveMongoRepository<Ubicacion, ObjectId> {
}
