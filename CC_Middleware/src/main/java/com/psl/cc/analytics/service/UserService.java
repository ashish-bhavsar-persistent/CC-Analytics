package com.psl.cc.analytics.service;

import java.util.List;
import java.util.Optional;

import com.psl.cc.analytics.model.CCUser;

public interface UserService {
	public CCUser findOneByUsername(String username);

	public CCUser save(CCUser user);

	public void delete(CCUser user);

	public Optional<CCUser> findOneById(String id);

	public List<CCUser> findAll();

	public List<CCUser> getAllByRoleName(String roleName);
	
	public long getAllUsersCountByRoleName(String roleName);

}
