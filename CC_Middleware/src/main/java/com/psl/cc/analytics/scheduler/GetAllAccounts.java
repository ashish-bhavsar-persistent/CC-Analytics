package com.psl.cc.analytics.scheduler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.psl.cc.analytics.constants.ControlCentreConstants;
import com.psl.cc.analytics.model.CC_User;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.repository.AccountsRepository;
import com.psl.cc.analytics.repository.ConfigurationRepository;
import com.psl.cc.analytics.service.RequestsAuditService;
import com.psl.cc.analytics.service.UserService;

@Component
public class GetAllAccounts {

	@Autowired
	private ConfigurationRepository configRepository;

	@Autowired
	private RequestsAuditService requestService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AccountsRepository accountsRepository;

	// It shoud be run at 12:00:00 am everyday
	@Scheduled(cron = "0 0 0 * * * ?")
	public void cronJob() {
		getAllAccounts();
	}

	public void initializeFirstTime() {
		if (requestService.getLatestRecord() == null) {
			System.out.println("Initializing First Time at " + new Date());
			getAllAccounts();
		}
	}

	private void getAllAccounts()  {
		List<CC_User> ccUsers = userService.findAll();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(ControlCentreConstants.NUMBER_OF_THREADS);
		List<Future> futureList = new ArrayList<Future>();
		for (CC_User ccUser : ccUsers) {
			Configuration configuration = configRepository.findOneByCC_UserId(ccUser.getId());
			if (configuration != null) {
				Future<JSONObject> futureObj = executor.submit(new FetchAccountDetails(ccUser, configuration, requestService, accountsRepository, executor));
				futureList.add(futureObj);
			}
		}
		
		
		
	}
}