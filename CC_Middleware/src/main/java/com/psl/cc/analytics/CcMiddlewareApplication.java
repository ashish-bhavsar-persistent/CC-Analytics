package com.psl.cc.analytics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.psl.cc.analytics.model.CC_User;
import com.psl.cc.analytics.model.Role;
import com.psl.cc.analytics.repository.RoleRepository;
import com.psl.cc.analytics.scheduler.GetAllAccounts;
import com.psl.cc.analytics.service.UserService;

@SpringBootApplication
public class CcMiddlewareApplication {

	public static void main(String[] args) {
		SpringApplication.run(CcMiddlewareApplication.class, args);
	}

	@Autowired
	@Qualifier("passwordEncoder")
	private PasswordEncoder passwordEncoder;

	@Autowired
	private GetAllAccounts getAccounts;

	@Bean
	CommandLineRunner init(RoleRepository roleRepository, UserService userService) {
		return args -> {
			Role adminRole = roleRepository.findOneByRole("ADMIN");
			if (adminRole == null) {
				adminRole = new Role();
				adminRole.setRole("ADMIN");
				roleRepository.save(adminRole);
			}

			Role userRole = roleRepository.findOneByRole("USER");
			if (userRole == null) {
				userRole = new Role();
				userRole.setRole("USER");
				roleRepository.save(userRole);
			}

			Role sysRole = roleRepository.findOneByRole("SYSADMIN");
			if (sysRole == null) {
				sysRole = new Role();
				sysRole.setRole("SYSADMIN");
				sysRole = roleRepository.save(sysRole);
			}

			CC_User user = userService.findOneByUsername("cc_sysadmin");
			if (user == null) {
				user = new CC_User();
				user.setUsername("cc_sysadmin");
				user.setName("Root User");
				user.setPassword(passwordEncoder.encode("password"));
				user.setCreatedOn(new Date());
				user.setLastUpdatedOn(new Date());
				user.setActive(true);
				List<Role> roles = new ArrayList<>();
				roles.add(sysRole);
				user.setRoles(roles);
				userService.save(user);
			}

			getAccounts.initializeFirstTime();
		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
