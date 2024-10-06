package com.provias.backend.service_user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @Id
    private ObjectId id;
    private String username;
    private String password;

    @JsonProperty("rolId")
    private ObjectId rolId;

    @Transient
    @JsonIgnore
    private Rol rol;
}
