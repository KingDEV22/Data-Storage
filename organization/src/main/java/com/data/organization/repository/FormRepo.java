package com.data.organization.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.data.organization.model.Form;

public interface FormRepo extends MongoRepository<Form,String> {
    
}
