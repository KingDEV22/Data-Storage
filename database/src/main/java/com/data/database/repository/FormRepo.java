package com.data.database.repository;

import java.util.List;
import java.util.Optional;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import com.data.database.model.Form;

@EnableScan
public interface FormRepo extends CrudRepository<Form, String> {
    Optional<Form> findByLink(String link);

    Optional<Form> findByName(String name);

    List<Form> findAllByOrgId(String orgId);
}
