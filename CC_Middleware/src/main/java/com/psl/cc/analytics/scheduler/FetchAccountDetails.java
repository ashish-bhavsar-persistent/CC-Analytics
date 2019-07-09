package com.psl.cc.analytics.scheduler;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psl.cc.analytics.constants.ControlCentreConstants;
import com.psl.cc.analytics.exception.CCException;
import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.model.CCUser;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.service.AccountService;
import com.psl.cc.analytics.service.RequestsAuditService;
import com.psl.cc.analytics.utils.APIAudits;

class FetchAccountDetails implements Callable<Map<String, AccountDTO>> {
	private static final Logger logger = LogManager.getLogger(FetchAccountDetails.class);

	private final CCUser ccUser;
	private final Configuration configuration;
	private final RequestsAuditService requestService;
	private final AccountService accountsService;
	private final APIAudits audit;
	private final RestTemplate restTemplate;

	public FetchAccountDetails(CCUser ccUser, Configuration configuration, RequestsAuditService requestService,
			AccountService accountsService, APIAudits audit, RestTemplate restTemplate) {
		this.ccUser = ccUser;
		this.configuration = configuration;
		this.requestService = requestService;
		this.accountsService = accountsService;
		this.audit = audit;
		this.restTemplate = restTemplate;
	}

	@Override
	public Map<String, AccountDTO> call() throws Exception {
		final Map<String, AccountDTO> accountsMap = new HashMap<>();
		List<AccountDTO> accountsList = accountsService.getAllByUserId(ccUser.getId());
		if (accountsList != null) {
			accountsList.forEach(accountDTO -> accountsMap.put(accountDTO.getAccountId(), accountDTO));
		}
		String username = ccUser.getUsername();
		String password = ccUser.getPassword();
		if (configuration.isUseAPIKey()) {
			password = configuration.getApiKey();
		}
		final HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(username, password);
		final HttpEntity<String> request = new HttpEntity<>(headers);

		extractDetails(accountsMap, request);

		return accountsMap;

	}

	private void extractDetails(final Map<String, AccountDTO> accountsMap, final HttpEntity<String> request)
			throws CCException {
		boolean lastPage = false;
		int pageNumber = 1;

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

				audit.doAudit("getAllAccounts", configuration.getBaseUrl() + ControlCentreConstants.ACCOUNTS_URL, null,
						params.toString(), ControlCentreConstants.STATUS_SUCCESS, ccUser, requestService);
				JSONObject accountsObject = new JSONObject(response.getBody());
				lastPage = accountsObject.getBoolean("lastPage");
				JSONArray accounts = accountsObject.getJSONArray("accounts");
				for (Object Obj : accounts) {
					JSONObject account = new JSONObject(Obj.toString());
					AccountDTO accountDTO = mapper.readValue(account.toString(), AccountDTO.class);
					if (accountsMap.containsKey(account.getString("accountId"))) {
						AccountDTO tempAccountDTO = accountsMap.get(account.getString("accountId"));
						accountDTO.setCreatedOn(tempAccountDTO.getCreatedOn());
						accountDTO.setDeviceList(tempAccountDTO.getDeviceList());
					}

					String accountId = account.getString("accountId");
					accountDTO.setUser(ccUser);
					accountDTO.setLastUpdatedOn(new Date());
					if (accountDTO.getCreatedOn() == null) {
						accountDTO.setCreatedOn(new Date());
						accountDTO.setDeviceList(new ArrayList<>());
					}
					accountsMap.put(accountId, accountDTO);
				}
				pageNumber++;

			} while (!lastPage);
		} catch (Exception e) {
			audit.doAudit("getAllAccounts", configuration.getBaseUrl() + ControlCentreConstants.ACCOUNTS_URL,
					e.getMessage(), params.toString(), ControlCentreConstants.STATUS_FAIL, ccUser, requestService);
			throw new CCException(e.getMessage());
		}
	}
}