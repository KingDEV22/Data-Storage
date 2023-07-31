package com.data.organization.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.data.organization.model.Question;

public interface QuestionRepo extends MongoRepository<Question, String> {
    
}
