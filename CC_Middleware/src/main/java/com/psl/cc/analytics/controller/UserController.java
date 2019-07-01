package com.psl.cc.analytics.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

import com.psl.cc.analytics.constants.ControlCentreConstants;
import com.psl.cc.analytics.exception.CCException;
import com.psl.cc.analytics.exception.ValidationException;
import com.psl.cc.analytics.model.CCUser;
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
		List<CCUser> ccUsers = userService.findAll();
		List<User> users = new ArrayList<>();

		for (CCUser cc_user : ccUsers) {
			Configuration configuration = configRepository.findOneByUserId(cc_user.getId());
			User tempUser = new User(cc_user, configuration);
			users.add(tempUser);
		}
		return users;
	}

	@GetMapping("/users/{id}")
	@PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
	@ApiOperation(value = "Search user by Id", response = User.class)
	public User getUserById(@PathVariable String id) throws CCException {
		Optional<CCUser> ccUser = userService.findOneById(id);
		User tempUser = null;
		if (ccUser.isPresent()) {
			Configuration configuration = configRepository.findOneByUserId(ccUser.get().getId());
			tempUser = new User(ccUser.get(), configuration);
		} else {
			throw new CCException(id + ControlCentreConstants.NOT_FOUND_MESSAGE);
		}
		return tempUser;
	}

	@PostMapping("/users")
	@ApiOperation(value = "Add a user")
	@PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
	public ResponseEntity<String> createUser(@RequestBody @Valid User user) throws CCException {
		if (user.isUseAPIKey() && user.isUsePassword()) {
			throw new ValidationException("Both useAPIKey and usePassword can not be true");
		} else if (!user.isUseAPIKey() && !user.isUsePassword()) {
			throw new ValidationException("Both useAPIKey and usePassword can not be false");
		}
		CCUser ccUser = new CCUser();
		List<Role> roles = new ArrayList<>();
		for (String role : user.getRoles()) {
			Role tempRole = roleRepository.findOneByName(role);
			if (tempRole == null) {
				throw new CCException(role + ControlCentreConstants.NOT_FOUND_MESSAGE);
			}
			roles.add(tempRole);
		}
		ccUser.setUsername(user.getUsername());
		ccUser.setName(user.getName());
		ccUser.setPassword(passwordEncoder.encode(user.getPassword()));
		ccUser.setActive(true);
		ccUser.setCreatedOn(new Date());
		ccUser.setLastUpdatedOn(new Date());
		ccUser.setRoles(roles);
		userService.save(ccUser);

		Configuration configuration = new Configuration();
		configuration.setBaseUrl(user.getBaseUrl());
		configuration.setBillingCyclePeriod(user.getBillingCyclePeriod());
		configuration.setBillingCycleStartDay(user.getBillingCycleStartDay());
		configuration.setCreatedOn(new Date());
		configuration.setLastUpdatedOn(new Date());
		configuration.setDeviceStates(user.getDeviceStates());
		configuration.setUseAPIKey(user.isUseAPIKey());
		configuration.setUsePassword(user.isUsePassword());
		configuration.setUser(ccUser);
		configuration.setApiKey(user.getApiKey());
		configRepository.save(configuration);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(ccUser.getId())
				.toUri();
		return ResponseEntity.created(location).build();
	}

	@DeleteMapping("/users/{id}")
	@ApiOperation(value = "Delete a user by Id")
	@PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
	public ResponseEntity<Void> deleteUserById(@PathVariable String id) throws CCException {
		Optional<CCUser> ccUser = userService.findOneById(id);
		if (ccUser.isPresent()) {
			Configuration configuration = configRepository.findOneByUserId(ccUser.get().getId());
			if (configuration != null)
				configRepository.delete(configuration);
			userService.delete(ccUser.get());
		} else {
			throw new CCException(id + ControlCentreConstants.NOT_FOUND_MESSAGE);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/users/{id}")
	@ApiOperation(value = "Update a user by Id")
	@PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
	public ResponseEntity<String> updateUser(@PathVariable String id, @Valid @RequestBody User user)
			throws CCException {
		if (user.isUseAPIKey() && user.isUsePassword()) {
			throw new ValidationException("Both useAPIKey and usePassword can not be true");
		} else if (!user.isUseAPIKey() && !user.isUsePassword()) {
			throw new ValidationException("Both useAPIKey and usePassword can not be false");
		}
		Optional<CCUser> optionalUser = userService.findOneById(id);
		if (optionalUser.isPresent()) {
			CCUser ccUser = optionalUser.get();
			List<Role> roles = new ArrayList<>();
			for (String role : user.getRoles()) {
				Role tempRole = roleRepository.findOneByName(role);
				if (tempRole == null) {
					throw new CCException(role + ControlCentreConstants.NOT_FOUND_MESSAGE);
				}
				roles.add(tempRole);
			}
			ccUser.setUsername(user.getUsername());
			ccUser.setName(user.getName());
			ccUser.setPassword(passwordEncoder.encode(user.getPassword()));
			ccUser.setActive(true);
			ccUser.setLastUpdatedOn(new Date());
			ccUser.setRoles(roles);

			userService.save(ccUser);

			Configuration configuration = configRepository.findOneByUserId(ccUser.getId());
			configuration.setBaseUrl(user.getBaseUrl());
			configuration.setBillingCyclePeriod(user.getBillingCyclePeriod());
			configuration.setBillingCycleStartDay(user.getBillingCycleStartDay());
			configuration.setCreatedOn(new Date());
			configuration.setLastUpdatedOn(new Date());
			configuration.setDeviceStates(user.getDeviceStates());
			configuration.setUseAPIKey(user.isUseAPIKey());
			configuration.setUsePassword(user.isUsePassword());
			configuration.setUser(ccUser);
			configuration.setApiKey(user.getApiKey());
			configRepository.save(configuration);

			URI location = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(ccUser.getId()).toUri();
			return ResponseEntity.created(location).build();
		} else {
			throw new CCException(id + ControlCentreConstants.NOT_FOUND_MESSAGE);
		}
	}

	@GetMapping("/users/me")
	@ApiOperation(value = "Get current user details")
	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = (String) authentication.getPrincipal();
		CCUser ccUser = userService.findOneByUsername(username);
		Configuration configuration = configRepository.findOneByUserId(ccUser.getId());
		return new User(ccUser, configuration);

	}
}
