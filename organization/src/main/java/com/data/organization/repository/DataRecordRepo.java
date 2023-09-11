package com.data.organization.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.data.organization.model.DataRecord;

public interface DataRecordRepo extends MongoRepository<DataRecord , String> {
        
}
