package com.data.organization.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.data.organization.model.OrgUser;

public interface OrgUserRepository extends MongoRepository<OrgUser, String> {
    Optional<OrgUser> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}