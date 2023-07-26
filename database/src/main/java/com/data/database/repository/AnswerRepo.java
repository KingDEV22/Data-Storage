package com.data.database.repository;

import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;

import com.data.database.model.Answer;

@EnableScan
public interface AnswerRepo extends DynamoDBCrudRepository<Answer, String> {
}
