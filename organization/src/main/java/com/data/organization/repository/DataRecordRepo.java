package com.data.organization.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.data.organization.model.DataRecord;

public interface DataRecordRepo extends MongoRepository<DataRecord , String> {
    @Query("{'metaDataId': ?0}")
    List<DataRecord> findAllByMetaDataId(String metaDataId);
}
