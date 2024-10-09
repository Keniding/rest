package com.provias.backend.service_staff.repository;

import com.provias.backend.service_staff.model.Personal;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PersonalRepository extends ReactiveMongoRepository<Personal, ObjectId> {
}
