package com.psl.cc.analytics.scheduler;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psl.cc.analytics.constants.ControlCentreConstants;
import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.model.CC_User;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.model.Device;
import com.psl.cc.analytics.model.RequestsAudit;
import com.psl.cc.analytics.repository.AccountsRepository;
import com.psl.cc.analytics.service.RequestsAuditService;

import org.springframework.web.util.UriComponentsBuilder;

class FetchAccountDetails implements Callable<JSONObject> {

	private final CC_User ccUser;
	private final Configuration configuration;
	private final RequestsAuditService requestService;
	private final AccountsRepository accountsRepository;
	private final ThreadPoolExecutor executor;

	public FetchAccountDetails(CC_User ccUser, Configuration configuration, RequestsAuditService requestService,
			AccountsRepository accountsRepository, ThreadPoolExecutor executor) {
		this.ccUser = ccUser;
		this.configuration = configuration;
		this.requestService = requestService;
		this.accountsRepository = accountsRepository;
		this.executor = executor;
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
		ResponseEntity<String> response =null;
		ObjectMapper mapper = new ObjectMapper();
		do {
			uri = UriComponentsBuilder.fromUriString(url).queryParam("pageNumber", String.valueOf(pageNumber))
					.build().toUri();
			response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
			if (response.getStatusCode() == HttpStatus.OK) {
				doAudit("getAllAccounts", configuration.getBaseUrl() + ControlCentreConstants.ACCOUNTS_URL, null, ControlCentreConstants.STATUS_SUCCESS);
				JSONObject accountsObject = new JSONObject(response.getBody().toString());
				System.out.println(accountsObject);
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
		System.out.println(accountsMap.size());
		
		Map<String, Future<Optional<String>>> accountFutureMap = new HashMap<String, Future<Optional<String>>>();
		List<Future<JSONObject>> futureListOfDevices = new ArrayList<>();

		for (String accountId : accountsMap.keySet()) {
			Future<Optional<String>> future = executor.submit(new FetchDevicesOfAccount(ccUser, configuration,requestService ,accountId, accountsMap));
			accountFutureMap.put(accountId, future);
		}
		
		System.out.println(accountsMap);

		
		for( String accountIdKey : accountFutureMap.keySet()) {
			Future<Optional<String>> accountFutureObj = accountFutureMap.get(accountIdKey);
			Optional<String> accountJson = accountFutureObj.get();
			for(Device deviceDto : accountsMap.get(accountIdKey).getDeviceList()) {
				Future<JSONObject> future = executor
						.submit(new GetDeviceDetails(ccUser, configuration,accountIdKey, deviceDto.getIccId(), accountsMap));
				futureListOfDevices.add(future);
			}
			
		}

		for (Future<JSONObject> deviceFutureObj : futureListOfDevices) {
			JSONObject deviceObj = deviceFutureObj.get();
			System.out.println(deviceObj);
		}
		System.out.println("PRINTING ACCOUNTS MAP BEFORE SAVING IT"+accountsMap);
		accountsRepository.saveAll(accountsMap.values());

		doAudit("getAllAccounts", configuration.getBaseUrl() + ControlCentreConstants.ACCOUNTS_URL, "e.getMessage() here ", ControlCentreConstants.STATUS_FAIL);

		return null;

	}

	private void doAudit(String apiName, String endpointUrl, String errorDetails, String status) {
		RequestsAudit audit = new RequestsAudit();
		audit.setApiName(apiName);
		audit.setCreatedOn(new Date());
		audit.setEndpointUrl(endpointUrl);
		audit.setErrorDetails(errorDetails);
		audit.setLastUpdatedOn(new Date());
		audit.setStatus(status);
		audit.setUser(ccUser);
		requestService.save(audit);
	}
}