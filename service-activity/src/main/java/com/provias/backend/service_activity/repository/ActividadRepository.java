package com.provias.backend.service_activity.repository;

import com.provias.backend.service_activity.model.Actividad;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ActividadRepository extends ReactiveMongoRepository<Actividad, ObjectId> {
}
