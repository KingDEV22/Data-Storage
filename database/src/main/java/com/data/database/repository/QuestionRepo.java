package com.data.database.repository;

import java.util.List;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import com.data.database.model.Question;

@EnableScan
public interface QuestionRepo extends CrudRepository<Question, String> {
    List<Question> findAllByFId(String fId);
}
