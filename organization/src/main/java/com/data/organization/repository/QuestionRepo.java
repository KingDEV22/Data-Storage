package com.data.organization.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.data.organization.model.Question;

public interface QuestionRepo extends MongoRepository<Question, String> {
    @Query("{'fid': ?0}")
    List<Question> findAllByFid(String fid);

    @Query("{'qid' : ?0 , 'fid' : ?1}")
    Question findByQidAndFid(String qid, String fid);
    @Query("{'fid': ?0}")
    void deleteAllByFid(String fid);

}
