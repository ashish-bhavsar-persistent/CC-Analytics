package com.psl.cc.analytics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.psl.cc.analytics.model.AccountDTO;

public interface AccountsRepository extends MongoRepository<AccountDTO, String> {

}
