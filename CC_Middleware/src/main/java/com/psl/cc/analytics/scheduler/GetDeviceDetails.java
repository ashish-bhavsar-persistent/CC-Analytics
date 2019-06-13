package com.psl.cc.analytics.scheduler;

import java.net.URI;
import java.util.concurrent.Callable;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.psl.cc.analytics.model.CC_User;
import com.psl.cc.analytics.model.Configuration;

public class GetDeviceDetails implements Callable<JSONObject> {
	private CC_User ccUser;
	private Configuration configuration;
	private String accountId;
	private String deviceId;

	public GetDeviceDetails(CC_User ccUser, Configuration configuration,String accountId, String deviceId) {
		this.ccUser = ccUser;
		this.configuration = configuration;
		this.accountId = accountId;
		this.deviceId = deviceId;
	}

	@Override
	public JSONObject call() throws Exception {
		String username = ccUser.getUsername();
		String password = ccUser.getPassword();
		if (configuration.isUseAPIKey()) {
			password = configuration.getApiKey();
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(username, password);
		HttpEntity<String> request = new HttpEntity<String>(headers);
		RestTemplate restTemplate = new RestTemplate();
		String url = ("https://rws-jpotest.jasperwireless.com/rws" + "/api/v1/devices/");
		URI uri  = UriComponentsBuilder.fromUriString(url).path(deviceId).build(true).toUri();

		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			JSONObject deviceObject = new JSONObject(response.getBody().toString());
			deviceObject.put("sp_id", accountId);
			return deviceObject;
		}
		return null;
	}

}
