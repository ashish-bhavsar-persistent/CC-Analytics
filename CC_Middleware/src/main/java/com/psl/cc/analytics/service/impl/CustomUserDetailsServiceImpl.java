package com.psl.cc.analytics.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.psl.cc.analytics.model.CCUser;
import com.psl.cc.analytics.model.TokenUser;
import com.psl.cc.analytics.service.UserService;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserService userSevice;

	@Override
	public UserDetails loadUserByUsername(String username) {
		CCUser ccUser = userSevice.findOneByUsername(username);
		if (ccUser == null) {
			throw new UsernameNotFoundException("User Not Found");
		}
		return new TokenUser(ccUser);
		
	}

}
