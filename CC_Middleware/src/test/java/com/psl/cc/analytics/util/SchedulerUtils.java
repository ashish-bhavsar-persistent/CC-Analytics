package com.psl.cc.analytics.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.psl.cc.analytics.model.CCUser;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.model.Role;
import com.psl.cc.analytics.repository.ConfigurationRepository;
import com.psl.cc.analytics.repository.RoleRepository;
import com.psl.cc.analytics.service.UserService;

@Service
public class SchedulerUtils {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private ConfigurationRepository configurationRepository;

	@Autowired
	org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

	public void setup() {
		tearDown();
		Role adminRole = new Role("ADMIN");
		roleRepository.save(adminRole);

		Role userRole = new Role("USER");
		roleRepository.save(userRole);

		Role sysRole = new Role("SYSADMIN");
		sysRole = roleRepository.save(sysRole);
//
		List<Role> sysRoles = new ArrayList<>();
		sysRoles.add(sysRole);

		List<Role> adminRoles = new ArrayList<>();
		adminRoles.add(adminRole);

		CCUser cc_sysadmin = new CCUser("cc_sysadmin", "cc_sysadmin", passwordEncoder.encode("password"), sysRoles,
				true);
		userService.save(cc_sysadmin);

		CCUser vivoSpAdmin = new CCUser("VivoSpAdmin", "VivoSpAdmin", passwordEncoder.encode("password"), adminRoles,
				true);
		userService.save(vivoSpAdmin);
		
		List<Role> rolesUser = new ArrayList<>();
		rolesUser.add(userRole);
		CCUser test = new CCUser("Vivo Test", "100002218", passwordEncoder.encode("password"), rolesUser, true);
		userService.save(test);

		List<String> status = new ArrayList<>();
		status.add("Activated");
		Configuration config1 = new Configuration(vivoSpAdmin, "https://rws-jpotest.jasperwireless.com/rws", 1, 31,
				status, false, true, "1edddb0c-06f6-41d4-9bad-2e2d38f26ae1");
		configurationRepository.save(config1);
	}

	public void tearDown() {
		mongoTemplate.getDb().drop();
	}
}
