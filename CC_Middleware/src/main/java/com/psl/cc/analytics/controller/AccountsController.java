package com.psl.cc.analytics.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.model.CC_User;
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

	@GetMapping("/account/names")
	@ApiOperation(value = "List of Account Names Associated with Current User", response = List.class)
	public List<AccountDTO> getAccountNames() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = (String) authentication.getPrincipal();
		CC_User ccUser = userService.findOneByUsername(username);
		return accountService.getAllAccountNames(ccUser.getId());
	}

}
