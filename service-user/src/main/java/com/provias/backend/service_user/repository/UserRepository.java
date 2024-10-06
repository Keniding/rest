package com.provias.backend.service_user.repository;

import com.provias.backend.service_user.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, ObjectId> {
}
