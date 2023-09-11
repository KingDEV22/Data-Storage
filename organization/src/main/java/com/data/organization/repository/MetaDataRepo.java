package com.data.organization.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.data.organization.model.MetaData;

public interface MetaDataRepo extends MongoRepository<MetaData, String> {
    @Query("{'orgId': ?0, 'type' : ?1}")
    List<MetaData> findAllByorgIdAndType(String orgId, String type);

    @Query("{'name' : ?0, 'orgId' : ?1}")
    Optional<MetaData> findByNameAndOrgId(String name, String orgId);

    Optional<MetaData> findByLink(String link);

    boolean existsByName(String name);

    @Query("{'name' : ?0, 'orgId' : ?1}")
    void deleteByNameAndOrgId(String formName, String orgId);
}
