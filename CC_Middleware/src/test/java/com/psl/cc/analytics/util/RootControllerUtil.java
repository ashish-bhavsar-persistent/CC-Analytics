package com.psl.cc.analytics.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.psl.cc.analytics.model.CCUser;
import com.psl.cc.analytics.model.Role;
import com.psl.cc.analytics.repository.RoleRepository;
import com.psl.cc.analytics.service.UserService;

@Service
public class RootControllerUtil {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserService userService;

	@Autowired
	org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;
	
	public void setup() {
		tearDown();

		Role adminRole = new Role("ADMIN");
		roleRepository.save(adminRole);

		Role sysadminRole = new Role("SYSADMIN");
		roleRepository.save(sysadminRole);

		List<Role> rolesSysadmin = new ArrayList<>();
		rolesSysadmin.add(sysadminRole);
		CCUser test = new CCUser("Root User", "cc_sysadmin", passwordEncoder.encode("password"), rolesSysadmin, true);
		userService.save(test);

		List<Role> roles = new ArrayList<>();
		roles.add(adminRole);
		CCUser user = new CCUser("Vivo Sp Admin", "VivoSpAdmin", passwordEncoder.encode("password"), roles, true);
		userService.save(user);

	}

	public void tearDown() {
		mongoTemplate.getDb().drop();
	}
}
