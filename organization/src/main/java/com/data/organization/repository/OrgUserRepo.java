package com.data.organization.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.data.organization.model.OrgUser;

public interface OrgUserRepo extends MongoRepository<OrgUser, String> {
    Optional<OrgUser> findByEmail(String email);

    @Query(value = "{ 'email' : ?0 }", fields = "{ 'orgId' : 1, 'country' : 1 }")
    Optional<OrgUser> findByEmailIncludeOrgIdndCountryFields(String email);

    Boolean existsByName(String username);

    Boolean existsByEmail(String email);
}