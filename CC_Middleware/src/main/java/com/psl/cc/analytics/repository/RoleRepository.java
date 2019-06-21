package com.psl.cc.analytics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.psl.cc.analytics.model.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
	Role findOneByName(String name);
}
