package com.provias.backend.service_post.repository;

import com.provias.backend.service_post.model.Cargo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CargoRepository extends ReactiveMongoRepository<Cargo, ObjectId> {
}
