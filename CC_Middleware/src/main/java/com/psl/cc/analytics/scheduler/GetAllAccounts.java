package com.psl.cc.analytics.scheduler;

import java.util.ArrayList;
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
		List<CC_User> cc_users = userService.findAll();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(ControlCentreConstants.numberOfThreadsForAccounts);
		List<Future> futureList = new ArrayList<Future>();
		for (CC_User cc_user : cc_users) {
			Configuration configuration = configRepository.findOneByCC_UserId(cc_user.getId());
			if (configuration != null) {
				Future<JSONObject> futureObj = executor.submit(new FetchAccountDetails(cc_user, configuration, requestService, accountsRepository));
				futureList.add(futureObj);
			}
		}
		for(Future future : futureList) {
			
//			JSONObject accountsObject;
//			try {
//				accountsObject = (JSONObject) future.get();
//				JSONArray accountsArray = accountsObject.getJSONArray("accounts");
//				System.out.println("ACCOUNTS ARRAY: "+accountsArray);
//			} catch (InterruptedException | ExecutionException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
			
		}
	}
}