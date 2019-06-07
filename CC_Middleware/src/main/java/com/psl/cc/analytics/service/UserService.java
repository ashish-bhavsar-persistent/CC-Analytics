package com.psl.cc.analytics.service;

import java.util.List;
import java.util.Optional;

import com.psl.cc.analytics.model.CC_User;

public interface UserService {
	CC_User findOneByUsername(String username);

	CC_User save(CC_User user);

	void delete(CC_User user);

	Optional<CC_User> findOneById(String id);

	List<CC_User> findAll();

}
