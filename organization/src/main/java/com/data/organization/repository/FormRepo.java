package com.data.organization.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.data.organization.model.Form;

public interface FormRepo extends MongoRepository<Form, String> {
    @Query("{'orgId': ?0}")
    List<Form> findAllByorgId(String orgId);

    Optional<Form> findByName(String name);

    Optional<Form> findByLink(String link);

    boolean existsByName(String name);
}
