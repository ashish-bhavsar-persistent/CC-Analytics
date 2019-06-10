package com.psl.cc.analytics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.psl.cc.analytics.model.Account;

public interface AccountsRepository extends MongoRepository<Account, String> {

}
