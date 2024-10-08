package com.provias.backend.service_type.repository;

import com.provias.backend.service_type.model.Tipo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TipoRepository extends ReactiveMongoRepository<Tipo, ObjectId> {
}
