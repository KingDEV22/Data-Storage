package com.data.database.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.data.database.model.Form;

public interface FormRepo extends MongoRepository<Form, String> {
    Optional<Form> findByLink(String link);
}
