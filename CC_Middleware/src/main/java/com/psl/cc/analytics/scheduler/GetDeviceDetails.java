package com.psl.cc.analytics.scheduler;

import java.net.URI;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psl.cc.analytics.constants.ControlCentreConstants;
import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.model.CC_User;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.model.Device;
import com.psl.cc.analytics.service.RequestsAuditService;
import com.psl.cc.analytics.utils.APIAudits;

public class GetDeviceDetails implements Callable<Optional<String>> {
	private static final Logger logger = LogManager.getLogger(GetDeviceDetails.class);
	private final CC_User ccUser;
	private final Configuration configuration;
	private final String accountId;
	private final String deviceId;
	private final AccountDTO account;
	private final APIAudits audit;
	private final RequestsAuditService requestService;

	public GetDeviceDetails(CC_User ccUser, Configuration configuration, String accountId, String deviceId,
			AccountDTO account, APIAudits audit, RequestsAuditService requestService) {
		this.ccUser = ccUser;
		this.configuration = configuration;
		this.accountId = accountId;
		this.deviceId = deviceId;
		this.account = account;
		this.audit = audit;
		this.requestService = requestService;

	}

	@Override
	public Optional<String> call() throws Exception {
		String username = ccUser.getUsername();
		String password = ccUser.getPassword();
		if (configuration.isUseAPIKey()) {
			password = configuration.getApiKey();
		}
		final HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(username, password);
		final HttpEntity<String> request = new HttpEntity<String>(headers);
		RestTemplate restTemplate = new RestTemplate();
		String url = configuration.getBaseUrl() + ControlCentreConstants.DEVICES_URL + "/";
		URI uri = UriComponentsBuilder.fromUriString(url).path(deviceId).build(true).toUri();
		ObjectMapper mapper = new ObjectMapper();
		JSONObject params = new JSONObject();
		try {
			params.put("accountId", accountId);
			ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
			if (response.getStatusCode() == HttpStatus.OK) {
				audit.doAudit("get Device Details",
						configuration.getBaseUrl() + ControlCentreConstants.DEVICES_URL + "/", null, params.toString(),
						ControlCentreConstants.STATUS_SUCCESS, ccUser, requestService);
				JSONObject deviceObject = new JSONObject(response.getBody().toString());
				Device deviceJson = mapper.readValue(deviceObject.toString(), Device.class);
				for (Device deviceFromAccount :account.getDeviceList()) {
					if (deviceJson.getIccid().equals(deviceFromAccount.getIccid())) {
						deviceJson.setCreatedOn(deviceFromAccount.getCreatedOn());
						BeanUtils.copyProperties(deviceFromAccount, deviceJson);
						deviceFromAccount.setLastUpdatedOn(new Date());
					}
				}
				return null;
			}
		} catch (Exception e) {
			logger.error(e);
			audit.doAudit("get Device Details", configuration.getBaseUrl() + ControlCentreConstants.DEVICES_URL + "/",
					e.getMessage(), params.toString(), ControlCentreConstants.STATUS_FAIL, ccUser, requestService);
			throw e;
		} 
		return null;
	}

}
