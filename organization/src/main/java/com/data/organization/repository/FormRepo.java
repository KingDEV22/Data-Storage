package com.data.organization.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.data.organization.model.Form;

public interface FormRepo extends MongoRepository<Form, String> {
    @Query("{'orgId': ?0}")
    List<Form> findAllByorgId(String orgId);

    @Query("{'name' : ?0, 'orgId' : ?1}")
    Optional<Form> findByNameAndOrgId(String name, String orgId);

    Optional<Form> findByLink(String link);

    boolean existsByName(String name);

    @Query("{'name' : ?0, 'orgId' : ?1}")
    void deleteByNameAndOrgId(String formName, String orgId);
}
