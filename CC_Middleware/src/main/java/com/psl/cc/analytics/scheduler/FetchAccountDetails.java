package com.psl.cc.analytics.scheduler;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
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
import com.psl.cc.analytics.model.RequestsAudit;
import com.psl.cc.analytics.repository.AccountsRepository;
import com.psl.cc.analytics.service.RequestsAuditService;

class FetchAccountDetails implements Callable<JSONObject> {

	private CC_User cc_user;
	private Configuration configuration;
	private RequestsAuditService requestService;
	private AccountsRepository accountsRepository;
	
	public FetchAccountDetails(CC_User cc_user, Configuration configuration, RequestsAuditService requestService, AccountsRepository accountsRepository) {
		this.cc_user = cc_user;
		this.configuration = configuration;
		this.requestService = requestService;
		this.accountsRepository = accountsRepository;
	}

	@Override
	public JSONObject call() throws Exception {
		String username = cc_user.getUsername();
		String password = cc_user.getPassword();
		if (configuration.isUseAPIKey()) {
			password = configuration.getApiKey();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(username, password);
		HttpEntity<String> request = new HttpEntity<String>(headers);

		try {
			RestTemplate restTemplate = new RestTemplate();
			URI url = new URI(configuration.getBaseUrl() + ControlCentreConstants.ACCOUNTS_URL);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
			if (response.getStatusCode() == HttpStatus.OK) {
				doAudit("getAllAccounts", configuration.getBaseUrl() + ControlCentreConstants.ACCOUNTS_URL, null, ControlCentreConstants.STATUS_SUCCESS);
				JSONObject accountsObject = new JSONObject(response.getBody().toString());
				while(!accountsObject.getBoolean("lastPage")){
					ObjectMapper mapper = new ObjectMapper();
					JSONArray accountsArray = accountsObject.getJSONArray("accounts");
					List<Future> futureList = new ArrayList<Future>();
					ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(ControlCentreConstants.numberOfThreadsForDevices);
					for(int i = 0 ; i<accountsArray.length(); i++) {
						AccountDTO account = mapper.readValue(accountsObject.getJSONArray("accounts").getJSONObject(i).toString(), AccountDTO.class);
						account.setUser(cc_user);
						Future<JSONObject> futureObj = executor.submit(new FetchDeviceDetails(cc_user, configuration, requestService, accountsRepository));
						futureList.add(futureObj);						
					}
				}
				
				return accountsObject;
			}
		} catch (Exception e) {
			doAudit("getAllAccounts", configuration.getBaseUrl() + ControlCentreConstants.ACCOUNTS_URL, e.getMessage(), ControlCentreConstants.STATUS_FAIL);
			throw e;
		}
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
		audit.setUser(cc_user);
		requestService.save(audit);
	}
}