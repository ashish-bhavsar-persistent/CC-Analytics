package com.psl.cc.analytics.scheduler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.psl.cc.analytics.constants.ControlCentreConstants;
import com.psl.cc.analytics.model.CC_User;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.repository.ConfigurationRepository;
import com.psl.cc.analytics.service.AccountService;
import com.psl.cc.analytics.service.RequestsAuditService;
import com.psl.cc.analytics.service.UserService;
import com.psl.cc.analytics.utils.APIAudits;

@Component
public class GetAllAccounts {
	private static final Logger logger = LogManager.getLogger(GetAllAccounts.class);
	@Autowired
	private ConfigurationRepository configRepository;

	@Autowired
	private RequestsAuditService requestService;

	@Autowired
	private UserService userService;

	@Autowired
	private AccountService accountsService;

	final DateFormat dateFormat = new SimpleDateFormat(ControlCentreConstants.DATEFORMAT_DEVICESURL);

	// It shoud be run at 12:00:00 am everyday
	@Scheduled(cron = "0 0 0 * * * ?")
	public void cronJob() {
		logger.debug("Cron Job Started {}", new Date());
		String modifiedSince = null;
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		if (requestService.getLatestRecord() == null) {
			c.add(Calendar.MONTH, -100);
			modifiedSince = dateFormat.format(c.getTime());
		} else {
			modifiedSince = dateFormat.format(c.getTime());
		}
		logger.info("value of modifiedSince is {}", modifiedSince);
		getAllAccounts(modifiedSince);
	}

	public void initializeFirstTime() {
		if (requestService.getLatestRecord() == null) {
			logger.debug("initializeFirstTime method invoked at {}", new Date());
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			c.add(Calendar.MONTH, -100);
			String modifiedSince = dateFormat.format(c.getTime());
			logger.info("value of modifiedSince is {}", modifiedSince);
			getAllAccounts(modifiedSince);
		}
	}

	private void getAllAccounts(String modifiedSince) {
		List<CC_User> ccUsers = userService.findAll();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
				.newFixedThreadPool(ControlCentreConstants.NUMBER_OF_THREADS);
		List<Future> futureList = new ArrayList<Future>();
		APIAudits audit = new APIAudits();
		logger.info("number of Users are {}", ccUsers.size());
		for (CC_User ccUser : ccUsers) {
			Configuration configuration = configRepository.findOneByCC_UserId(ccUser.getId());
			if (configuration != null) {
				Future<JSONObject> futureObj = executor.submit(new FetchAccountDetails(ccUser, configuration,
						requestService, accountsService, executor, audit, modifiedSince));
				futureList.add(futureObj);
			}
		}

//		for (Future<JSONObject> futureObj : futureList) {
//			try {
//				futureObj.get();
//			} catch (Exception e) {
//				CC_APIException apiException = (CC_APIException) e;
//				audit.doAudit(apiException.getApiName(), apiException.getEndpointUrl(), apiException.getErrorDetails(),
//						apiException.getParams(), apiException.getStatus(), apiException.getCcUser(), requestService);
//
//			}
//		}

	}
}