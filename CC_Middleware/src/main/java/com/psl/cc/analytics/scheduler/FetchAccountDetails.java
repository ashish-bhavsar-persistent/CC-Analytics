package com.psl.cc.analytics.scheduler;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psl.cc.analytics.APIAudits;
import com.psl.cc.analytics.constants.ControlCentreConstants;
import com.psl.cc.analytics.exception.CC_APIException;
import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.model.CC_User;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.model.Device;
import com.psl.cc.analytics.model.RequestsAudit;
import com.psl.cc.analytics.repository.AccountsRepository;
import com.psl.cc.analytics.service.RequestsAuditService;

class FetchAccountDetails implements Callable<JSONObject> {
	private static final Logger logger = LogManager.getLogger(FetchAccountDetails.class);

	private final CC_User ccUser;
	private final Configuration configuration;
	private final RequestsAuditService requestService;
	private final AccountsRepository accountsRepository;
	private final ThreadPoolExecutor executor;
	private final APIAudits audit;
	private final String modifiedSince;

	public FetchAccountDetails(CC_User ccUser, Configuration configuration, RequestsAuditService requestService,
			AccountsRepository accountsRepository, ThreadPoolExecutor executor, APIAudits audit, String modifiedSince) {
		this.ccUser = ccUser;
		this.configuration = configuration;
		this.requestService = requestService;
		this.accountsRepository = accountsRepository;
		this.executor = executor;
		this.audit = audit;
		this.modifiedSince = modifiedSince;
	}

	@Override
	public JSONObject call() throws Exception {
		String username = ccUser.getUsername();
		String password = ccUser.getPassword();
		if (configuration.isUseAPIKey()) {
			password = configuration.getApiKey();
		}
		final HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(username, password);
		final HttpEntity<String> request = new HttpEntity<String>(headers);
		final Map<String, AccountDTO> accountsMap = new HashMap<>();
		boolean lastPage = false;
		int pageNumber = 1;
		RestTemplate restTemplate = new RestTemplate();
		final String url = configuration.getBaseUrl() + ControlCentreConstants.ACCOUNTS_URL;
		URI uri = null;
		ResponseEntity<String> response = null;
		ObjectMapper mapper = new ObjectMapper();
		JSONObject params = new JSONObject();
		try {
			do {
				uri = UriComponentsBuilder.fromUriString(url).queryParam("pageNumber", String.valueOf(pageNumber))
						.build().toUri();
				params.put("pageNumber", pageNumber);
				response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
				if (response.getStatusCode() == HttpStatus.OK) {
					audit.doAudit("getAllAccounts", configuration.getBaseUrl() + ControlCentreConstants.ACCOUNTS_URL,
							null, params.toString(), ControlCentreConstants.STATUS_SUCCESS, ccUser, requestService);
					JSONObject accountsObject = new JSONObject(response.getBody().toString());
					lastPage = accountsObject.getBoolean("lastPage");
					JSONArray accounts = accountsObject.getJSONArray("accounts");
					for (Object Obj : accounts) {
						JSONObject account = new JSONObject(Obj.toString());
						AccountDTO accountDTO = mapper.readValue(account.toString(), AccountDTO.class);
						String accountId = account.getString("accountId");
						accountsMap.put(accountId, accountDTO);
					}
					pageNumber++;
				}
			} while (!lastPage);
		} catch (Exception e) {
			logger.error(e);
			audit.doAudit("getAllAccounts", configuration.getBaseUrl() + ControlCentreConstants.ACCOUNTS_URL,
					e.getMessage(), params.toString(), ControlCentreConstants.STATUS_FAIL, ccUser, requestService);
			throw e;
		}

		Map<String, Future<Optional<String>>> accountFutureMap = new HashMap<String, Future<Optional<String>>>();
		List<Future<JSONObject>> futureListOfDevices = new ArrayList<>();

		for (String accountId : accountsMap.keySet()) {
			Future<Optional<String>> future = executor.submit(new FetchDevicesOfAccount(ccUser, configuration,
					requestService, accountId, accountsMap, audit, modifiedSince));
			accountFutureMap.put(accountId, future);
		}

		for (String accountIdKey : accountFutureMap.keySet()) {
			Future<Optional<String>> accountFutureObj = accountFutureMap.get(accountIdKey);
			try {
				accountFutureObj.get();
				for (Device deviceDto : accountsMap.get(accountIdKey).getDeviceList()) {
					Future<JSONObject> future = executor.submit(new GetDeviceDetails(ccUser, configuration,
							accountIdKey, deviceDto.getIccid(), accountsMap, audit, requestService));
					futureListOfDevices.add(future);
				}
			} catch (Exception e) {
				throw e;
			}

		}

		for (Future<JSONObject> deviceFutureObj : futureListOfDevices) {
			try {
				JSONObject deviceObj = deviceFutureObj.get();
			} catch (Exception e) {
				throw e;
			}
		}
		accountsRepository.saveAll(accountsMap.values());
		System.out.println("Execution Done");
		return null;
	}
}