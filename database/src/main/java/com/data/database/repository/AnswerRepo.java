package com.data.database.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.data.database.model.Answer;

@Repository
public interface AnswerRepo extends MongoRepository<Answer, String> {
}
