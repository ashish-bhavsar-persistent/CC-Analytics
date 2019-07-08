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
import org.springframework.web.client.RestTemplate;

import com.psl.cc.analytics.model.CCUser;
import com.psl.cc.analytics.model.Role;
import com.psl.cc.analytics.repository.RoleRepository;
import com.psl.cc.analytics.service.UserService;

@SpringBootApplication
public class CcMiddlewareApplication {

	public static void main(String[] args) {
		SpringApplication.run(CcMiddlewareApplication.class, args);
	}

	@Autowired
	@Qualifier("passwordEncoder")
	private PasswordEncoder passwordEncoder;

	@Bean
	CommandLineRunner init(RoleRepository roleRepository, UserService userService) {
		return args -> {
			Role adminRole = roleRepository.findOneByName("ADMIN");
			if (adminRole == null) {
				adminRole = new Role();
				adminRole.setName("ADMIN");
				roleRepository.save(adminRole);
			}

			Role userRole = roleRepository.findOneByName("USER");
			if (userRole == null) {
				userRole = new Role();
				userRole.setName("USER");
				roleRepository.save(userRole);
			}

			Role sysRole = roleRepository.findOneByName("SYSADMIN");
			if (sysRole == null) {
				sysRole = new Role();
				sysRole.setName("SYSADMIN");
				sysRole = roleRepository.save(sysRole);
			}

			CCUser user = userService.findOneByUsername("cc_sysadmin");
			if (user == null) {
				user = new CCUser();
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
		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
