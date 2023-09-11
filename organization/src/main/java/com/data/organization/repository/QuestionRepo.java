package com.data.organization.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.data.organization.model.Question;

public interface QuestionRepo extends MongoRepository<Question, String> {
    @Query("{'metaDataId': ?0}")
    List<Question> findAllByMetaDataId(String metaDataId);

    @Query("{'questionId' : ?0 , 'metaDataId' : ?1}")
    Question findByQuestionIdAndMetaDataId(String questionId, String metaDataId);
    @Query("{'metaDataId': ?0}")
    void deleteAllByMetaDataId(String metaDataId);

}
