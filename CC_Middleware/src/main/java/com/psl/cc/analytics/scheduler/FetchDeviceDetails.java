package com.psl.cc.analytics.scheduler;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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
import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.model.CC_User;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.model.Device;
import com.psl.cc.analytics.repository.AccountsRepository;
import com.psl.cc.analytics.service.RequestsAuditService;

public class FetchDeviceDetails implements Callable<JSONObject>{

	private CC_User cc_user;
	private Configuration configuration;
	private RequestsAuditService requestService;
	private AccountsRepository accountsRepository;
	
	
	public FetchDeviceDetails(CC_User cc_user, Configuration configuration, RequestsAuditService requestService,
			AccountsRepository accountsRepository) {
		super();
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
		
		RestTemplate restTemplate = new RestTemplate();
		URI url = new URI(configuration.getBaseUrl() + ControlCentreConstants.DEVICES_URL);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
		ObjectMapper mapper = new ObjectMapper();
		List<Device> deviceList = new ArrayList<Device>();
		if (response.getStatusCode() == HttpStatus.OK) {
			JSONObject devicesObject = new JSONObject(response.getBody().toString());			
			JSONArray devicesArray = devicesObject.getJSONArray("devices");
			for(int i=0 ; i<devicesArray.length() ; i++) {
				
				Device device = mapper.readValue(devicesArray.get(i).toString(), Device.class);
				deviceList.add(device);
			}
			
			System.out.println(" DEVICE LIST LENGTH : "+ deviceList.size());
			
			
		}
		
		
		
		return null;
	}

}
