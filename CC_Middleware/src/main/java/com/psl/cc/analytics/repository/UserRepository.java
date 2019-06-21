package com.psl.cc.analytics.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.psl.cc.analytics.model.CCUser;

public interface UserRepository extends MongoRepository<CCUser, String> {
	CCUser findOneByUsername(String username);
	Optional<CCUser> findOneById(String id);
}
