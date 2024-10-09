package com.provias.backend.service_milestone.repository;

import com.provias.backend.service_milestone.model.Hito;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface HitoRepository extends ReactiveMongoRepository<Hito, ObjectId> {
}
