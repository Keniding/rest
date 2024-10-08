package com.provias.backend.service_category.repository;

import com.provias.backend.service_category.model.Categoria;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoriaRepository extends ReactiveMongoRepository<Categoria, ObjectId> {
}
