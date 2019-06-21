package com.psl.cc.analytics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.psl.cc.analytics.model.Configuration;

public interface ConfigurationRepository extends MongoRepository<Configuration, String> {
	@Query("{'user.id': ?0}")
	Configuration findOneByUserId(String userId);

}
