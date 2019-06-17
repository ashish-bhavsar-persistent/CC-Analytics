package com.psl.cc.analytics.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.repository.AccountsRepository;
import com.psl.cc.analytics.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

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

}
