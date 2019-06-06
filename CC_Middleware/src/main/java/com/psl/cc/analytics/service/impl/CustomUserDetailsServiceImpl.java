package com.psl.cc.analytics.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.psl.cc.analytics.model.CC_User;
import com.psl.cc.analytics.model.TokenUser;
import com.psl.cc.analytics.service.UserService;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserService userSevice;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		CC_User cc_user = userSevice.findOneByUsername(username);
		if (cc_user == null) {
			throw new UsernameNotFoundException("User Not Found");
		}
		TokenUser tokenUser =  new TokenUser(cc_user);
		return tokenUser;
	}

}
