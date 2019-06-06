package com.psl.cc.analytics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.psl.cc.analytics.model.CC_User;

public interface UserRepository extends MongoRepository<CC_User, String> {
	CC_User findOneByUsername(String username);
}
