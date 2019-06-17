package com.psl.cc.analytics.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.psl.cc.analytics.model.AccountDTO;

public interface AccountsRepository extends MongoRepository<AccountDTO, String> {
	@Query("{'user.id': ?0}")
	List<AccountDTO> getAllByUserId(String userId);
}
