package com.psl.cc.analytics.scheduler;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.util.UriComponentsBuilder;

import com.psl.cc.analytics.constants.ControlCentreConstants;
import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.model.CC_User;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.model.Device;
import com.psl.cc.analytics.model.RequestsAudit;
import com.psl.cc.analytics.service.RequestsAuditService;

public class FetchDevicesOfAccount implements Callable<Optional<String>> {

	private final String accountId;
	private final CC_User ccUser;
	private final Configuration configuration;
	private final RequestsAuditService requestService;
	private final Map<String, AccountDTO> accountsMap;

	public FetchDevicesOfAccount(CC_User ccUser, Configuration configuration, RequestsAuditService requestService,
			String accountId, Map<String, AccountDTO> accountsMap) {
		this.ccUser = ccUser;
		this.configuration = configuration;
		this.accountId = accountId;
		this.requestService = requestService;
		this.accountsMap = accountsMap;
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
		final String url = configuration.getBaseUrl() + ControlCentreConstants.DEVICES_URL;
		boolean lastPage = false;
		int pageNumber = 1;
		final DateFormat dateFormat = new SimpleDateFormat(ControlCentreConstants.DATEFORMAT_DEVICESURL);
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, -200);
		String modifiedDate = dateFormat.format(c.getTime());
		System.out.println("DATE " + modifiedDate);
		URI uri = null;
		ResponseEntity<String> response = null;
		List<Device> deviceDTOList = new ArrayList<Device>();
		do {
			uri = UriComponentsBuilder.fromUriString(url).queryParam("pageNumber", String.valueOf(pageNumber))
					.queryParam("modifiedSince", URLEncoder.encode("2000-06-12T13:44:28+05:30", StandardCharsets.UTF_8.toString()))
					.queryParam("accountId", accountId).build(true).toUri();
			response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
			if (response.getStatusCode() == HttpStatus.OK) {
				doAudit("getAllDevicesForAccount", configuration.getBaseUrl() + ControlCentreConstants.DEVICES_URL,
						null, ControlCentreConstants.STATUS_SUCCESS);
				JSONObject deviceObject = new JSONObject(response.getBody().toString());
				lastPage = deviceObject.getBoolean("lastPage");
				JSONArray devicesArray = deviceObject.getJSONArray("devices");
				for (int i = 0; i < devicesArray.length(); i++) {	
					Device deviceObj = new Device();
					deviceObj.setIccId(devicesArray.getJSONObject(i).getString("iccid"));
					deviceDTOList.add(deviceObj);
				}
				pageNumber++;
			}
		} while (!lastPage);
		accountsMap.get(accountId).setDeviceList(deviceDTOList);
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
