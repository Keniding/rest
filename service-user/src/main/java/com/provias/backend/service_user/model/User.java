package com.provias.backend.service_user.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {
    @Id
    private ObjectId id;
    private String username;
    private String password;
    private boolean active;
    private Rol rol;
}
