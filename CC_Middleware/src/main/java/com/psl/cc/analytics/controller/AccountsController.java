package com.psl.cc.analytics.controller;

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.psl.cc.analytics.constants.ControlCentreConstants;
import com.psl.cc.analytics.exception.ValidationException;
import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.model.CCUser;
import com.psl.cc.analytics.model.Device;
import com.psl.cc.analytics.response.AccountAggregation;
import com.psl.cc.analytics.service.AccountService;
import com.psl.cc.analytics.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { "Accounts" })
public class AccountsController {

	private static final Logger logger = LogManager.getLogger(AccountsController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private AccountService accountService;

	@PreAuthorize("hasAuthority('ROLE_SYSADMIN') || hasAuthority('ROLE_ADMIN')")
	@GetMapping("/accounts/name")
	@ApiOperation(value = "List of Account Names Associated with Admin Id", response = List.class)
	public List<AccountDTO> getAccountNames(@RequestParam(required = false, defaultValue = "") String adminId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = (String) authentication.getPrincipal();
		Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
		CCUser ccUser = userService.findOneByUsername(username);
		boolean isSysadmin = roles.stream()
				.anyMatch(auth -> auth.getAuthority().equals(ControlCentreConstants.SYSADMIN_AUTHORITY));
		if (isSysadmin && (adminId.isEmpty())) {
			throw new ValidationException("Admin Id is Mandetory for SYSADMIN Role");
		} else if (!isSysadmin) {
			adminId = ccUser.getId();
		}
		return accountService.getAllAccountNames(adminId);
	}

	@PreAuthorize("hasAuthority('ROLE_SYSADMIN') || hasAuthority('ROLE_ADMIN')")
	@GetMapping("/accounts/ratePlan")
	@ApiOperation(value = "Get Rate Plan Count for Service Provider", response = List.class)
	public List<AccountAggregation> getRatePlanCount(
			@RequestParam(required = false, defaultValue = "") String adminId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = (String) authentication.getPrincipal();
		CCUser ccUser = userService.findOneByUsername(username);
		Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
		boolean isSysadmin = roles.stream()
				.anyMatch(auth -> auth.getAuthority().equals(ControlCentreConstants.SYSADMIN_AUTHORITY));
		if (isSysadmin && (adminId.isEmpty())) {
			throw new ValidationException("Admin Id is Mandetory for SYSADMIN Role");
		} else if (!isSysadmin) {
			adminId = ccUser.getId();
		}
		return accountService.getAccountRatePlanOrCommCountPlan(adminId, ControlCentreConstants.ACCOUNT_RATE_PLAN,
				ControlCentreConstants.DEVICE_RATE_PLAN);
	}

	@PreAuthorize("hasAuthority('ROLE_SYSADMIN') || hasAuthority('ROLE_ADMIN')")
	@GetMapping("/accounts/commPlan")
	@ApiOperation(value = "Get Communication Plan Count for Service Provider", response = List.class)
	public List<AccountAggregation> getCommPlanCount(
			@RequestParam(required = false, defaultValue = "") String adminId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = (String) authentication.getPrincipal();
		CCUser ccUser = userService.findOneByUsername(username);
		Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
		boolean isSysadmin = roles.stream()
				.anyMatch(auth -> auth.getAuthority().equals(ControlCentreConstants.SYSADMIN_AUTHORITY));
		if (isSysadmin && (adminId.isEmpty())) {
			throw new ValidationException("Admin Id is Mandetory for SYSADMIN Role");
		} else if (!isSysadmin) {
			adminId = ccUser.getId();
		}
		return accountService.getAccountRatePlanOrCommCountPlan(adminId, ControlCentreConstants.ACCOUNT_COMM_PLAN,
				ControlCentreConstants.DEVICE_COMM_PLAN);
	}

	@PreAuthorize("hasAuthority('ROLE_SYSADMIN') || hasAuthority('ROLE_ADMIN')")
	@GetMapping("/accounts/deviceStatus")
	@ApiOperation(value = "Get Device Status Count for Service Provider", response = List.class)
	public List<Device> getDeviceStatus(@RequestParam(required = false, defaultValue = "") String adminId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = (String) authentication.getPrincipal();
		CCUser ccUser = userService.findOneByUsername(username);
		Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
		boolean isSysadmin = roles.stream()
				.anyMatch(auth -> auth.getAuthority().equals(ControlCentreConstants.SYSADMIN_AUTHORITY));
		if (isSysadmin && (adminId.isEmpty())) {
			throw new ValidationException("Admin Id is Mandetory for SYSADMIN Role");
		} else if (!isSysadmin) {
			adminId = ccUser.getId();
		}
		return accountService.getDeviceStatusCountByUserId(adminId);
	}
}
