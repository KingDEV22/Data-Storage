package com.data.organization.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.data.organization.model.Form;

@Repository
public interface FormRepo extends MongoRepository<Form, String> {

}
