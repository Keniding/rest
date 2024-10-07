package com.provias.backend.service_project.repository;

import com.provias.backend.service_project.model.Proyecto;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProyectoRepository extends ReactiveMongoRepository<Proyecto, ObjectId> {
}
