package com.provias.backend.service_resource.repository;

import com.provias.backend.service_resource.model.Recurso;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface RecursoRepository extends ReactiveMongoRepository<Recurso, ObjectId> {
}
