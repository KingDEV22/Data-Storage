package com.data.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.data.database.model.Question;

public interface QuestionRepo extends MongoRepository<Question, String> {
    @Query("{'fId': ?0}")
    List<Question> findAllByfId(String fId);
}
