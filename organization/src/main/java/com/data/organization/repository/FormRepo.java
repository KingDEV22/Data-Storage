package com.data.organization.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.data.organization.model.Form;

@Repository
public interface FormRepo extends MongoRepository<Form, String> {
    @Query("{'orgId': ?0}")
    List<Form> findAllByorgId(String orgId);

    @Query("{'name': ?0}")
    Optional<Form> findByName(String name);
}
