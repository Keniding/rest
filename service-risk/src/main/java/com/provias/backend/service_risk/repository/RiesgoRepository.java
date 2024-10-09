package com.provias.backend.service_risk.repository;

import com.provias.backend.service_risk.model.Riesgo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface RiesgoRepository extends ReactiveMongoRepository<Riesgo, ObjectId> {
}
