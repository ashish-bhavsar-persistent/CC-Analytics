package com.psl.cc.analytics.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.repository.AccountsRepository;
import com.psl.cc.analytics.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

	private static final int String = 0;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private AccountsRepository repository;

	@Override
	public List<AccountDTO> saveAll(Iterable<AccountDTO> accounts) {
		return repository.saveAll(accounts);

	}

	@Override
	public List<AccountDTO> getAllByUserId(String userId) {

		return repository.getAllByUserId(userId);
	}

	@Override
	public List<AccountDTO> getAllAccountNames(String userId) {
		return repository.getAllAccountNames(userId);
	}

}
