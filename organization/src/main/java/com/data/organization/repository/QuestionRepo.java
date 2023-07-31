package com.data.organization.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.data.organization.model.Question;

@Repository
public interface QuestionRepo extends MongoRepository<Question, String> {
    
}
