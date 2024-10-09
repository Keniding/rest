package com.provias.backend.service_staff.repository;

import com.provias.backend.service_staff.model.Cargo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CargoRepository extends ReactiveMongoRepository<Cargo, ObjectId> {
}
