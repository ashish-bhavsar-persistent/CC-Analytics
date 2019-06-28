package com.psl.cc.analytics.scheduler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.psl.cc.analytics.constants.ControlCentreConstants;
import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.model.CCUser;
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
	@Scheduled(cron = "0 0 0 * * *", zone = "Indian/Maldives")
	public void cronJob() {
		logger.debug("Cron Job Started {}", new Date());
		String modifiedSince = null;
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		if (requestService.getLatestRecord() == null) {
			c.set(c.get(Calendar.YEAR), 0, 1, 0, 0, 0);
			modifiedSince = dateFormat.format(c.getTime());
		} else {
			c.add(Calendar.DATE, -1);
			c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 0, 0, 0);
			modifiedSince = dateFormat.format(c.getTime());
		}
		logger.info("value of modifiedSince is {}", modifiedSince);
		getAllAccounts(modifiedSince);
	}

	public void initializeFirstTime() {
		if (requestService.getLatestRecord() == null) {
			logger.debug("initializeFirstTime method invoked at {}", new Date());
			Calendar c = Calendar.getInstance();
			c.set(c.get(Calendar.YEAR), 0, 1, 0, 0, 0);
			String modifiedSince = dateFormat.format(c.getTime());
			logger.info("value of modifiedSince is {}", modifiedSince);
			getAllAccounts(modifiedSince);
		}
	}

	private void getAllAccounts(String modifiedSince) {
		List<CCUser> ccUsers = userService.findAll();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
				.newFixedThreadPool(ControlCentreConstants.NUMBER_OF_THREADS);
		List<Future<Map<String, AccountDTO>>> futureList = new ArrayList<>();
		APIAudits audit = new APIAudits();
		final Map<String, AccountDTO> accountsMap = new HashMap<>();
		final Map<String, Configuration> configMap = new HashMap<>();
		logger.info("number of Users are {}", ccUsers.size());
		// get All Account
		for (CCUser ccUser : ccUsers) {
			Configuration configuration = configRepository.findOneByUserId(ccUser.getId());
			if (configuration != null) {
				configMap.put(ccUser.getId(), configuration);
				Future<Map<String, AccountDTO>> futureObj = executor
						.submit(new FetchAccountDetails(ccUser, configuration, requestService, accountsService, audit));
				futureList.add(futureObj);
			}
		}

		futureList.forEach(futureObj -> {
			try {
				Map<String, AccountDTO> tempAccountsMap = futureObj.get();
				accountsMap.putAll(tempAccountsMap);
			} catch (Exception e) {
				logger.error(e);
			}
		});
		logger.info("{} accounts are retrieved from {} users", accountsMap.size(), ccUsers.size());
		logger.debug("Execution of getAllAccounts API is Done.");
		// Search Device
		final List<Future<Optional<String>>> futureListOfDevices = new ArrayList<>();

		accountsMap.forEach((accountId, accountDTO) -> {
			Configuration configuration = configMap.get(accountDTO.getUser().getId());
			Future<Optional<String>> future = executor.submit(new FetchDevicesOfAccount(configuration, requestService,
					accountId, accountDTO, audit, modifiedSince));
			futureListOfDevices.add(future);

		});
		logger.debug("length of futureListOfDevices is {}", futureListOfDevices.size());
		futureListOfDevices.forEach(futureObj -> {
			try {
				futureObj.get();
			} catch (Exception e) {
				logger.error(e);
			}
		});
		logger.debug("Execution of searchDevices API is Done.");

		// get Device Details
		futureListOfDevices.clear();
		accountsMap.forEach((accountId, accountDTO) -> {
			Configuration configuration = configMap.get(accountDTO.getUser().getId());

			if (accountDTO != null && accountDTO.getDeviceList() != null) {

				accountDTO.getDeviceList().forEach(device -> {
					Future<Optional<String>> future = executor.submit(new GetDeviceDetails(accountDTO.getUser(),
							configuration, accountId, device.getIccid(), accountDTO, audit, requestService));
					futureListOfDevices.add(future);
				});
			}

		});

		futureListOfDevices.forEach(futureObj -> {
			try {
				futureObj.get();
			} catch (Exception e) {
				logger.error(e);
			}
		});
		logger.debug("Execution of getDeviceDetails API is Done.");
		accountsService.saveAll(accountsMap.values());
		logger.info("{} records are stored into database", accountsMap.size());
		logger.debug("Terminating thread pool");
		executor.shutdownNow();
	}
}