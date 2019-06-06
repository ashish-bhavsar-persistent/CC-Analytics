package com.psl.cc.analytics.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.psl.cc.analytics.model.CC_User;
import com.psl.cc.analytics.repository.UserRepository;
import com.psl.cc.analytics.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository repository;

	@Override
	public CC_User findOneByUsername(String username) {
		return repository.findOneByUsername(username);
	}

	@Override
	public CC_User save(CC_User user) {
		return repository.save(user);
	}

}
