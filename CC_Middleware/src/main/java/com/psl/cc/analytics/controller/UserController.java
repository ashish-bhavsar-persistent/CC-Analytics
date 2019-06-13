package com.psl.cc.analytics.controller;

import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.psl.cc.analytics.exception.CC_Exception;
import com.psl.cc.analytics.exception.InAppropriateDataException;
import com.psl.cc.analytics.model.CC_User;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.model.Role;
import com.psl.cc.analytics.repository.ConfigurationRepository;
import com.psl.cc.analytics.repository.RoleRepository;
import com.psl.cc.analytics.response.User;
import com.psl.cc.analytics.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { "Users" })
public class UserController {

	private static final Logger logger = LogManager.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private ConfigurationRepository configRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	@Qualifier("passwordEncoder")
	private PasswordEncoder passwordEncoder;

	@PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
	@GetMapping("/users")
	@ApiOperation(value = "List of users", response = List.class)
	public List<User> getAllUsers() {
		logger.debug("getAllUsers() invoked");
		List<CC_User> cc_Users = userService.findAll();
		List<User> users = new ArrayList<User>();
		if (cc_Users != null)
			for (CC_User cc_user : cc_Users) {
				Configuration configuration = configRepository.findOneByCC_UserId(cc_user.getId());
				User tempUser = new User(cc_user, configuration);
				users.add(tempUser);
			}
		return users;
	}

	@GetMapping("/users/{id}")
	@PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
	@ApiOperation(value = "Search user by Id", response = User.class)
	public User getUserById(@PathVariable String id) {
		Optional<CC_User> cc_User = userService.findOneById(id);
		User tempUser = null;
		if (cc_User.isPresent()) {
			Configuration configuration = configRepository.findOneByCC_UserId(cc_User.get().getId());
			tempUser = new User(cc_User.get(), configuration);
		} else {
			throw new CC_Exception(id + " Not Found");
		}
		return tempUser;
	}

	@PostMapping("/users")
	@ApiOperation(value = "Add a user")
	@PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
	public ResponseEntity<?> createUser(@RequestBody @Valid User user) {
		CC_User cc_User = new CC_User();
		List<Role> roles = new ArrayList<>();
		for (String role : user.getRoles()) {
			Role tempRole = roleRepository.findOneByRole(role);
			if (tempRole == null) {
				return new ResponseEntity<>(role + " Not Found", HttpStatus.NOT_FOUND);
			}
			roles.add(tempRole);
		}
		cc_User.setUsername(user.getUsername());
		cc_User.setName(user.getName());
		cc_User.setPassword(passwordEncoder.encode(user.getPassword()));
		cc_User.setActive(true);
		cc_User.setCreatedOn(new Date());
		cc_User.setLastUpdatedOn(new Date());
		cc_User.setRoles(roles);
		userService.save(cc_User);

		Configuration configuration = new Configuration();
		configuration.setBaseUrl(user.getBaseUrl());
		configuration.setBillingCyclePeriod(user.getBillingCyclePeriod());
		configuration.setBillingCycleStartDay(user.getBillingCycleStartDay());
		configuration.setCreatedOn(new Date());
		configuration.setLastUpdatedOn(new Date());
		configuration.setDeviceStates(user.getDeviceStates());
		configuration.setUseAPIKey(user.isUseAPIKey());
		configuration.setUsePassword(user.isUsePassword());
		configuration.setUser(cc_User);
		configuration.setApiKey(user.getApiKey());
		configRepository.save(configuration);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(cc_User.getId())
				.toUri();
		return ResponseEntity.created(location).build();
	}

	@DeleteMapping("/users/{id}")
	@ApiOperation(value = "Delete a user by Id")
	@PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
	public ResponseEntity<Void> deleteUserById(@PathVariable String id) {
		Optional<CC_User> cc_User = userService.findOneById(id);
		if (cc_User.isPresent()) {
			Configuration configuration = configRepository.findOneByCC_UserId(cc_User.get().getId());
			configRepository.delete(configuration);
			userService.delete(cc_User.get());
		} else {
			throw new CC_Exception(id + " Not Found");
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@PutMapping("/users/{id}")
	@ApiOperation(value = "Update a user by Id")
	@PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
	public ResponseEntity<?> updateUser(@PathVariable String id, @Valid @RequestBody User user) {
		if (user.isUseAPIKey() && user.isUsePassword()) {
			throw new InAppropriateDataException("Both useAPIKey and usePassword can not be true");
		} else if (!user.isUseAPIKey() && !user.isUsePassword()) {
			throw new InAppropriateDataException("Both useAPIKey and usePassword can not be false");
		}
		Optional<CC_User> cc_UserOptional = userService.findOneById(id);
		if (cc_UserOptional.isPresent()) {
			CC_User cc_User = cc_UserOptional.get();
			List<Role> roles = new ArrayList<>();
			for (String role : user.getRoles()) {
				Role tempRole = roleRepository.findOneByRole(role);
				if (tempRole == null) {
					return new ResponseEntity<>(role + " Not Found", HttpStatus.NOT_FOUND);
				}
				roles.add(tempRole);
			}
			cc_User.setUsername(user.getUsername());
			cc_User.setName(user.getName());
			cc_User.setPassword(passwordEncoder.encode(user.getPassword()));
			cc_User.setActive(true);
			cc_User.setLastUpdatedOn(new Date());
			cc_User.setRoles(roles);

			userService.save(cc_User);

			Configuration configuration = configRepository.findOneByCC_UserId(cc_User.getId());
			configuration.setBaseUrl(user.getBaseUrl());
			configuration.setBillingCyclePeriod(user.getBillingCyclePeriod());
			configuration.setBillingCycleStartDay(user.getBillingCycleStartDay());
			configuration.setCreatedOn(new Date());
			configuration.setLastUpdatedOn(new Date());
			configuration.setDeviceStates(user.getDeviceStates());
			configuration.setUseAPIKey(user.isUseAPIKey());
			configuration.setUsePassword(user.isUsePassword());
			configuration.setUser(cc_User);
			configuration.setApiKey(user.getApiKey());
			configRepository.save(configuration);

			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
					.buildAndExpand(cc_User.getId()).toUri();
			return ResponseEntity.created(location).build();
		} else {
			return new ResponseEntity<>(id + " Not Found", HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/users/me")
	@ApiOperation(value = "Get current user details")
	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = (String) authentication.getPrincipal();
		System.out.println("logged in user name:: " + authentication.getPrincipal());

		CC_User cc_User = userService.findOneByUsername(username);
		User tempUser = null;
		if (cc_User != null) {
			Configuration configuration = configRepository.findOneByCC_UserId(cc_User.getId());
			tempUser = new User(cc_User, configuration);
		} else {
			throw new CC_Exception(" Not Found");
		}
		return tempUser;
	}
}
