package com.psl.cc.analytics.scheduler;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.beanutils.BeanUtils;
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

public class GetDeviceDetails implements Callable<JSONObject> {
	private final CC_User ccUser;
	private final Configuration configuration;
	private final String accountId;
	private final String deviceId;
	private final Map<String, AccountDTO> accountsMap;

	public GetDeviceDetails(CC_User ccUser, Configuration configuration, String accountId, String deviceId,
			Map<String, AccountDTO> accountsMap) {
		this.ccUser = ccUser;
		this.configuration = configuration;
		this.accountId = accountId;
		this.deviceId = deviceId;
		this.accountsMap = accountsMap;
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
		RestTemplate restTemplate = new RestTemplate();
		String url = configuration.getBaseUrl() + ControlCentreConstants.DEVICES_URL + "/";
		URI uri = UriComponentsBuilder.fromUriString(url).path(deviceId).build(true).toUri();
		ObjectMapper mapper = new ObjectMapper();
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			JSONObject deviceObject = new JSONObject(response.getBody().toString());
			System.out.println(deviceObject);
			Device deviceJson = mapper.readValue(deviceObject.toString(), Device.class);
			for (Device deviceFromMap : accountsMap.get(accountId).getDeviceList()) {
				if (deviceJson.getIccid().equals(deviceFromMap.getIccid())) {
					BeanUtils.copyProperties(deviceFromMap, deviceJson);
				}
			}
			return deviceObject;
		}
		return null;
	}

}
