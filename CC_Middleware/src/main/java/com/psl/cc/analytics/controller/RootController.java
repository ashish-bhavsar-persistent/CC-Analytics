package com.psl.cc.analytics.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psl.cc.analytics.constants.ControlCentreConstants;
import com.psl.cc.analytics.model.CCUser;
import com.psl.cc.analytics.response.AdminsInfo;
import com.psl.cc.analytics.response.User;
import com.psl.cc.analytics.service.AccountService;
import com.psl.cc.analytics.service.DeviceService;
import com.psl.cc.analytics.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { "Service Providers Info" })
public class RootController {

	private static final Logger logger = LogManager.getLogger(RootController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private DeviceService deviceService;

	@PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
	@GetMapping("/admins/name")
	@ApiOperation(value = "List of Service Providers Names", response = List.class)
	public List<User> getAdminNames() {
		List<CCUser> ccUsers = userService.getAllByRoleName(ControlCentreConstants.SP_ADMIN_ROLE);
		List<User> users = new ArrayList<>();
		if (ccUsers != null && !ccUsers.isEmpty()) {
			ccUsers.forEach(ccUser -> {
				User user = new User();
				user.setName(ccUser.getName());
				user.setId(ccUser.getId());
				user.setUsername(ccUser.getUsername());
				users.add(user);
			});
		}
		return users;
	}

	@PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
	@GetMapping("/admins/stats")
	@ApiOperation(value = "It will provide count of Service Providers, Accounts and Devices", response = List.class)
	public List<AdminsInfo> getAdminStats() {
		long userCount = userService.getAllUsersCountByRoleName(ControlCentreConstants.SP_ADMIN_ROLE);
		long accountCount = accountService.getCount();
		long devicesCount = deviceService.getCount();

		List<AdminsInfo> infos = new ArrayList<>();
		AdminsInfo spInfo = new AdminsInfo("Service Provider", userCount);
		AdminsInfo accountInfo = new AdminsInfo("Accounts", accountCount);
		AdminsInfo deviceInfo = new AdminsInfo("Devices", devicesCount);
		
		infos.add(spInfo);
		infos.add(accountInfo);
		infos.add(deviceInfo);
		return infos;
	}
}
