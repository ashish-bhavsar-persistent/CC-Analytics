package com.psl.cc.analytics.controller;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psl.cc.analytics.constants.ControlCentreConstants;
import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.model.CCUser;
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

	@GetMapping("/accounts/name")
	@ApiOperation(value = "List of Account Names Associated with Current User", response = List.class)
	public List<AccountDTO> getAccountNames() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = (String) authentication.getPrincipal();
		CCUser ccUser = userService.findOneByUsername(username);
		return accountService.getAllAccountNames(ccUser.getId());
	}

	@GetMapping("/accounts/ratePlan")
	@ApiOperation(value = "Get yearly rate plan count for logged in service Provider", response = List.class)
	public List<AccountAggregation> getRatePlanCount() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = (String) authentication.getPrincipal();
		CCUser ccUser = userService.findOneByUsername(username);
		return accountService.getAccountRatePlanOrCommCountPlan(ccUser.getId(),
				ControlCentreConstants.ACCOUNT_RATE_PLAN, ControlCentreConstants.DEVICE_RATE_PLAN);
	}

	@GetMapping("/accounts/commPlan")
	@ApiOperation(value = "Get yearly communication plan count for logged in service Provider", response = List.class)
	public List<AccountAggregation> getCommPlanCount() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = (String) authentication.getPrincipal();
		CCUser ccUser = userService.findOneByUsername(username);
		return accountService.getAccountRatePlanOrCommCountPlan(ccUser.getId(),
				ControlCentreConstants.ACCOUNT_COMM_PLAN, ControlCentreConstants.DEVICE_COMM_PLAN);
	}

	@GetMapping("/accounts/deviceStatus")
	@ApiOperation(value = "Get yearly/monthly device status count for perticular account by passing granularity = MONTHLY/YEARLY", response = List.class)
	public List<AccountAggregation> getDeviceStatust() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = (String) authentication.getPrincipal();
		CCUser ccUser = userService.findOneByUsername(username);
		List<AccountAggregation> aggregations = accountService.getDeviceStatusCountByUserId(ccUser.getId(), "monthly");
		if (aggregations != null && !aggregations.isEmpty()) {
			List<AccountAggregation> yearlyAggregations = accountService.getDeviceStatusCountByUserId(ccUser.getId(),
					"yearly");
			aggregations.forEach(monthlyAggregation -> {
				Optional<AccountAggregation> agg = yearlyAggregations.stream()
						.filter(yearlyAgg -> yearlyAgg.getStatus().equals(monthlyAggregation.getStatus())).findFirst();
				if (agg.isPresent()) {
					monthlyAggregation.setMonthlyTotal(monthlyAggregation.getTotal());
					monthlyAggregation.setYearlyTotal(agg.get().getTotal());
					monthlyAggregation.setTotal(0l);
				}
			});
		}
		return aggregations;
	}
}
