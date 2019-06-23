package com.psl.cc.analytics.scheduler;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

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
import com.psl.cc.analytics.model.CCUser;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.model.Device;

public class FetchDeviceDetails implements Callable<JSONObject> {

	private CCUser ccUser;
	private Configuration configuration;

	public FetchDeviceDetails(CCUser ccUser, Configuration configuration) {
		super();
		this.ccUser = ccUser;
		this.configuration = configuration;

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
		HttpEntity<String> request = new HttpEntity<>(headers);

		RestTemplate restTemplate = new RestTemplate();
		URI url = new URI(configuration.getBaseUrl() + ControlCentreConstants.DEVICES_URL);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
		ObjectMapper mapper = new ObjectMapper();
		List<Device> deviceList = new ArrayList<>();
		if (response.getStatusCode() == HttpStatus.OK) {
			JSONObject devicesObject = new JSONObject(response.getBody());
			JSONArray devicesArray = devicesObject.getJSONArray("devices");
			for (int i = 0; i < devicesArray.length(); i++) {

				Device device = mapper.readValue(devicesArray.get(i).toString(), Device.class);
				deviceList.add(device);
			}

		}

		 return null;
	}

}
