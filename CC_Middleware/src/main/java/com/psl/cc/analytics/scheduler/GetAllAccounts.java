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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.psl.cc.analytics.constants.ControlCentreConstants;
import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.model.CCUser;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.model.Device;
import com.psl.cc.analytics.model.Role;
import com.psl.cc.analytics.repository.ConfigurationRepository;
import com.psl.cc.analytics.repository.RoleRepository;
import com.psl.cc.analytics.service.AccountService;
import com.psl.cc.analytics.service.DeviceService;
import com.psl.cc.analytics.service.RequestsAuditService;
import com.psl.cc.analytics.service.UserService;
import com.psl.cc.analytics.utils.APIAudits;

@org.springframework.context.annotation.Configuration
@EnableScheduling
public class GetAllAccounts {
	private static final Logger logger = LogManager.getLogger(GetAllAccounts.class);

	@Value("${defaultPassword}")
	private String defaultPassword;

	@Value("${modifiedSinceYear}")
	private int modifiedSinceYear;

	@Autowired
	private ConfigurationRepository configRepository;

	@Autowired
	private RequestsAuditService requestService;

	@Autowired
	private UserService userService;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	APIAudits audit;

	@Autowired
	private AccountService accountsService;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	@Qualifier("passwordEncoder")
	private PasswordEncoder passwordEncoder;

	final DateFormat dateFormat = new SimpleDateFormat(ControlCentreConstants.DATEFORMAT_DEVICESURL);

	// It shoud be run at 12:00:00 am everyday
	@Scheduled(cron = "0 0 0 * * *")
	public void cronJob() {
		logger.debug("Cron Job Started {}", new Date());
		String modifiedSince = null;
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, -1);
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 0, 0, 0);
		modifiedSince = dateFormat.format(c.getTime());
		logger.info("value of modifiedSince is {}", modifiedSince);
		getAllAccounts(modifiedSince, false);
	}

	@Scheduled(initialDelay = 1000 * 6, fixedDelay = Long.MAX_VALUE)
	public void initializeFirstTime() {

		Calendar c = Calendar.getInstance();
		c.set(modifiedSinceYear, 0, 1, 0, 0, 0);
		String modifiedSince = dateFormat.format(c.getTime());

		logger.debug("initializeFirstTime method invoked at {}", new Date());
		logger.info("value of modifiedSince is {}", modifiedSince);

		if (requestService.getLatestRecord() == null) {
			getAllAccounts(modifiedSince, true);
		}

	}

	private void getAllAccounts(String modifiedSince, boolean firstTime) {

		List<CCUser> ccUsers = userService.findAll();
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
				.newFixedThreadPool(ControlCentreConstants.NUMBER_OF_THREADS);
		List<Future<Map<String, AccountDTO>>> futureList = new ArrayList<>();

		final Map<String, Configuration> configMap = new HashMap<>();
		logger.info("number of Users are {}", ccUsers.size());
		for (CCUser ccUser : ccUsers) {
			Configuration configuration = configRepository.findOneByUserId(ccUser.getId());
			if (configuration != null) {
				configMap.put(ccUser.getId(), configuration);
				Future<Map<String, AccountDTO>> futureObj = executor.submit(new FetchAccountDetails(ccUser,
						configuration, requestService, accountsService, audit, restTemplate));
				futureList.add(futureObj);
				fetchAccountInfoFromFutureList(futureList, ccUsers, configMap, executor, modifiedSince, firstTime);
			}
		}

		logger.debug("Terminating thread pool");
		executor.shutdownNow();
	}

	private void fetchAccountInfoFromFutureList(List<Future<Map<String, AccountDTO>>> futureList, List<CCUser> ccUsers,
			Map<String, Configuration> configMap, ThreadPoolExecutor executor, String modifiedSince,
			boolean firstTime) {
		Role userRole = roleRepository.findOneByName(ControlCentreConstants.USER_ROLE);
		List<Role> roles = new ArrayList<>();
		roles.add(userRole);
		final Map<String, AccountDTO> accountsMap = new HashMap<>();

		futureList.forEach(futureObj -> {
			try {
				Map<String, AccountDTO> tempAccountsMap = futureObj.get();
				accountsMap.putAll(tempAccountsMap);
			} catch (Exception e) {
				logger.error(e);
			}
		});
		futureList.clear();
		logger.info("{} accounts are retrieved from {} users", accountsMap.size(), ccUsers.size());
		logger.debug("Execution of getAllAccounts API is Done.");

		accountsMap.keySet().forEach(key -> {
			CCUser user = userService.findOneByUsername(key);
			if (user == null) {
				AccountDTO dto = accountsMap.get(key);

				user = new CCUser(dto.getAccountName(), key, passwordEncoder.encode(defaultPassword), roles, true);
				userService.save(user);
			}
			user = null;
		});
		fetchDevicesOfAccount(accountsMap, configMap, executor, modifiedSince, firstTime);
		accountsMap.clear();
	}

	private void fetchDevicesOfAccount(Map<String, AccountDTO> accountsMap, Map<String, Configuration> configMap,
			ThreadPoolExecutor executor, String modifiedSince, boolean firstTime) {
		final List<Future<Optional<String>>> futureListOfDevices = new ArrayList<>();
		accountsMap.forEach((accountId, accountDTO) -> {
			Configuration configuration = configMap.get(accountDTO.getUser().getId());
			Future<Optional<String>> future = executor.submit(new FetchDevicesOfAccount(configuration, requestService,
					accountId, accountDTO, audit, modifiedSince, restTemplate));
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
		futureListOfDevices.clear();
		getDeviceDetails(accountsMap, configMap, executor, firstTime);
	}

	private void getDeviceDetails(Map<String, AccountDTO> accountsMap, Map<String, Configuration> configMap,
			ThreadPoolExecutor executor, boolean firstTime) {
		accountsMap.forEach((accountId, accountDTO) -> {
			Configuration configuration = configMap.get(accountDTO.getUser().getId());
			if (!accountDTO.getDeviceList().isEmpty()) {
				saveData(accountDTO, configuration, executor, firstTime);
			}
		});

	}

	private void saveData(AccountDTO accountDTO, Configuration configuration, ThreadPoolExecutor executor,
			boolean firstTime) {

		List<Future<Optional<String>>> futureListOfDevices = new ArrayList<>();
		accountDTO.getDeviceList().forEach(device -> {
			Future<Optional<String>> future = executor.submit(new GetDeviceDetails(accountDTO.getUser(), configuration,
					accountDTO.getAccountId(), device.getIccid(), accountDTO, audit, requestService, restTemplate));
			futureListOfDevices.add(future);
		});
		futureListOfDevices.forEach(futureObj -> {
			try {
				futureObj.get();
			} catch (Exception e) {
				logger.error(e);
			}
		});
		logger.debug("Execution of getDeviceDetails API is Done for {}", accountDTO.getAccountId());

		if (firstTime)
			deviceService.saveAll(accountDTO.getDeviceList());
		else {
			accountDTO.getDeviceList().forEach(device -> {
				if (device.getStatus() != null) {
					Optional<Device> optional = deviceService.findOneByIccid(device.getIccid());
					if (optional.isPresent()) {
						Device tempDevice = optional.get();
						device.setId(tempDevice.getId());
						device.setLastUpdatedOn(new Date());
						device.setCreatedOn(tempDevice.getCreatedOn());
						tempDevice = null;
					}
					deviceService.save(device);
				}
			});
		}
		accountDTO.setDeviceList(null);
		accountsService.save(accountDTO);
		logger.info("{} Records are stored into database for {}", futureListOfDevices.size(),
				accountDTO.getAccountId());
		futureListOfDevices.clear();
	}

}