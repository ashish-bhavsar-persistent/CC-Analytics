package com.psl.cc.analytics.service;

import com.psl.cc.analytics.model.CC_User;

public interface UserService {
	CC_User findOneByUsername(String username);

	CC_User save(CC_User user);
}
