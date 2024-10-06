package com.provias.backed.service_user.repository;

import com.provias.backed.service_user.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface UserRepository extends MongoRepository<User, UUID> {
}
