package com.psl.cc.analytics.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.psl.cc.analytics.model.CCUser;
import com.psl.cc.analytics.repository.UserRepository;
import com.psl.cc.analytics.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository repository;

	@Override
	public CCUser findOneByUsername(String username) {
		return repository.findOneByUsername(username);
	}

	@Override
	public CCUser save(CCUser user) {
		return repository.save(user);
	}

	@Override
	public void delete(CCUser user) {
		repository.delete(user);
	}

	@Override
	public Optional<CCUser> findOneById(String id) {
		return repository.findOneById(id);
	}

	@Override
	public List<CCUser> findAll() {
		return repository.findAll();
	}

}
