package com.data.organization.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.data.organization.model.Answer;


public interface AnswerRepo extends MongoRepository<Answer, String> {
}
