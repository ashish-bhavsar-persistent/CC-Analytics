package com.psl.cc.analytics.service;

import java.util.List;
import java.util.Optional;

import com.psl.cc.analytics.model.CCUser;

public interface UserService {
	CCUser findOneByUsername(String username);

	CCUser save(CCUser user);

	void delete(CCUser user);

	Optional<CCUser> findOneById(String id);

	List<CCUser> findAll();

}
