package com.psl.cc.analytics.model;

import org.springframework.security.core.userdetails.User;

public class TokenUser extends User {

	public TokenUser(CC_User user) {
		super(user.getUsername(), user.getPassword(), user.getUserAuthority());
	}

	private static final long serialVersionUID = -2758906045773936031L;

}
