package com.data.organization.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.data.organization.model.Question;

public interface QuestionRepo extends MongoRepository<Question, String> {
    @Query("{'mId': ?0}")
    List<Question> findAllByMId(String mId);

    @Query("{'qId' : ?0 , 'fId' : ?1}")
    Question findByQIdAndMId(String qId, String mId);
    @Query("{'mId': ?0}")
    void deleteAllByMId(String mId);

}
