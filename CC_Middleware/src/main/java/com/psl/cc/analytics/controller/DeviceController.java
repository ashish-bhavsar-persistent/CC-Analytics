package com.psl.cc.analytics.controller;

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.psl.cc.analytics.constants.ControlCentreConstants;
import com.psl.cc.analytics.exception.ValidationException;
import com.psl.cc.analytics.model.CCUser;
import com.psl.cc.analytics.model.Device;
import com.psl.cc.analytics.service.AccountService;
import com.psl.cc.analytics.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { "Devices" })
public class DeviceController {

	private static final Logger logger = LogManager.getLogger(DeviceController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private AccountService accountService;

	@GetMapping("/devices/ratePlan")
	@ApiOperation(value = "Get yearly rate plan count for perticular account", response = List.class)
	public List<Device> getRatePlanCount(@RequestParam(required = false, defaultValue = "") String adminId,
			@RequestParam(required = false, defaultValue = "") String accountId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = (String) authentication.getPrincipal();
		Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
		CCUser ccUser = userService.findOneByUsername(username);
		boolean isSysadmin = roles.stream()
				.anyMatch(auth -> auth.getAuthority().equals(ControlCentreConstants.SYSADMIN_AUTHORITY));
		boolean isAdmin = false;
		if (!isSysadmin)
			isAdmin = roles.stream()
					.anyMatch(auth -> auth.getAuthority().equals(ControlCentreConstants.ADMIN_AUTHORITY));
		if (isSysadmin && (adminId.isEmpty() || accountId.isEmpty())) {
			throw new ValidationException("Admin Id And Account Id is Mandetory for SYSADMIN Role");
		} else if (isAdmin) {
			if (accountId.isEmpty()) {
				throw new ValidationException("Account Id is Mandetory for ADMIN Role");
			} else {
				adminId = ccUser.getId();
			}
		} else if (!isSysadmin) {
			adminId = null;
			accountId = ccUser.getUsername();
		}

		return accountService.getDeviceRatePlanOrCommCountPlanByAccountId(adminId, accountId,
				ControlCentreConstants.DEVICE_RATE_PLAN);
	}

	@GetMapping("/devices/commPlan")
	@ApiOperation(value = "Get yearly communication plan count for perticular account", response = List.class)
	public List<Device> getCommPlanCount(@RequestParam(required = false, defaultValue = "") String adminId,
			@RequestParam(required = false, defaultValue = "") String accountId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String username = (String) authentication.getPrincipal();

		Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
		CCUser ccUser = userService.findOneByUsername(username);
		boolean isSysadmin = roles.stream()
				.anyMatch(auth -> auth.getAuthority().equals(ControlCentreConstants.SYSADMIN_AUTHORITY));
		boolean isAdmin = false;
		if (!isSysadmin)
			isAdmin = roles.stream()
					.anyMatch(auth -> auth.getAuthority().equals(ControlCentreConstants.ADMIN_AUTHORITY));
		if (isSysadmin && (adminId.isEmpty() || accountId.isEmpty())) {
			throw new ValidationException("Admin Id And Account Id is Mandetory for SYSADMIN Role");
		} else if (isAdmin) {
			if (accountId.isEmpty()) {
				throw new ValidationException("Account Id is Mandetory for ADMIN Role");
			} else {
				adminId = ccUser.getId();
			}
		} else if (!isSysadmin) {
			adminId = null;
			accountId = ccUser.getUsername();
		}

		return accountService.getDeviceRatePlanOrCommCountPlanByAccountId(adminId, accountId,
				ControlCentreConstants.DEVICE_COMM_PLAN);
	}

	@GetMapping("/devices/status")
	@ApiOperation(value = "Get yearly/monthly device status count for perticular account", response = List.class)
	public List<Device> getStatusCount(@RequestParam(required = false, defaultValue = "") String adminId,
			@RequestParam(required = false, defaultValue = "") String accountId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = (String) authentication.getPrincipal();

		Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
		CCUser ccUser = userService.findOneByUsername(username);
		boolean isSysadmin = roles.stream()
				.anyMatch(auth -> auth.getAuthority().equals(ControlCentreConstants.SYSADMIN_AUTHORITY));
		boolean isAdmin = false;
		if (!isSysadmin)
			isAdmin = roles.stream()
					.anyMatch(auth -> auth.getAuthority().equals(ControlCentreConstants.ADMIN_AUTHORITY));
		if (isSysadmin && (adminId.isEmpty() || accountId.isEmpty())) {
			throw new ValidationException("Admin Id And Account Id is Mandetory for SYSADMIN Role");
		} else if (isAdmin) {
			if (accountId.isEmpty()) {
				throw new ValidationException("Account Id is Mandetory for ADMIN Role");
			} else {
				adminId = ccUser.getId();
			}
		} else if (!isSysadmin) {
			adminId = null;
			accountId = ccUser.getUsername();
		}
		List<Device> statusYearly = accountService.getDeviceStatusCountByAccountId(adminId, accountId, "yearly");
		List<Device> statusMonthly = accountService.getDeviceStatusCountByAccountId(adminId, accountId, "monthly");
		statusYearly.forEach(device -> {
			Device monthly = statusMonthly.stream()
					.filter(monthlyData -> device.getStatus().equals(monthlyData.getStatus())).findAny().orElse(null);
			if(monthly!=null) {
				device.setMonthlyCount(monthly.getTotal());
			}
			device.setYearlyCount(device.getTotal());
			device.setTotal(null);
		});
		return statusYearly;
	}
}
