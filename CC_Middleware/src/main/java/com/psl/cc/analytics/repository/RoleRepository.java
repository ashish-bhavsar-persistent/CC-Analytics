package com.psl.cc.analytics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.psl.cc.analytics.model.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
	
	@Query(value = "{'name':?0}")
	Role findOneByName(String name);
}
