package com.psl.cc.analytics.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.model.CCUser;

public interface UserRepository extends MongoRepository<CCUser, String> {
	
	public CCUser findOneByUsername(String username);

	public Optional<CCUser> findOneById(String id);

	@Query(value = "{'roles.name': ?0}", fields = "{username:1, _id:1,name:1}")
	public List<CCUser> getAllByRoleName(String roleName);

	@Query(value = "{'roles.name': ?0}", count = true)
	public long getAllUsersCountByRoleName(String roleName);
	
}
