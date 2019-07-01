package com.psl.cc.analytics.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.psl.cc.analytics.model.CCUser;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.model.Role;
import com.psl.cc.analytics.repository.ConfigurationRepository;
import com.psl.cc.analytics.repository.RoleRepository;
import com.psl.cc.analytics.service.UserService;

@Service
public class UserDetailsUtil {
	
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

	private JSONObject data = new JSONObject();

	public JSONObject getData() {
		return data;
	}

	public void setup() {
		tearDown();
		Role adminRole = new Role("ADMIN");
		roleRepository.save(adminRole);

		Role userRole = new Role("USER");
		roleRepository.save(userRole);

		Role sysRole = new Role("SYSADMIN");
		sysRole = roleRepository.save(sysRole);

		List<Role> sysRoles = new ArrayList<>();
		sysRoles.add(sysRole);
		CCUser cc_sysadmin = new CCUser("cc_sysadmin", "cc_sysadmin", passwordEncoder.encode("password"), sysRoles,
				true);
		userService.save(cc_sysadmin);

		CCUser deleteUser = new CCUser("cc_sysadmin1", "cc_sysadmin1", passwordEncoder.encode("password"), sysRoles,
				true);
		userService.save(deleteUser);
		CCUser deleteUser2 = new CCUser("cc_sysadmin2", "cc_sysadmin2", passwordEncoder.encode("password"), sysRoles,
				true);
		userService.save(deleteUser2);
		
		List<String> status = new ArrayList<>();
		status.add("Activated");
		Configuration config = new Configuration(deleteUser2, "https://rws-jpotest.jasperwireless.com/rws", 1, 31, status, false, true, "1edddb0c-06f6-41d4-9bad-2e2d38f26ae1");
		configurationRepository.save(config);
		data.put("deleteUser", deleteUser.getId());
		data.put("deleteUser2", deleteUser2.getId());

	}

	public void tearDown() {
		mongoTemplate.getDb().drop();
	}
}
