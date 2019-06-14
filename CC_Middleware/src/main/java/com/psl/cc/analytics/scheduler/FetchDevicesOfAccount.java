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

import com.psl.cc.analytics.APIAudits;
import com.psl.cc.analytics.constants.ControlCentreConstants;
import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.model.CC_User;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.model.Device;
import com.psl.cc.analytics.model.RequestsAudit;
import com.psl.cc.analytics.service.RequestsAuditService;

public class FetchDevicesOfAccount implements Callable<Optional<String>> {
	private static final Logger logger = LogManager.getLogger(FetchDevicesOfAccount.class);
	private final String accountId;
	private final CC_User ccUser;
	private final Configuration configuration;
	private final RequestsAuditService requestService;
	private final Map<String, AccountDTO> accountsMap;
	private final APIAudits audit;
	private final String modifiedSince;

	public FetchDevicesOfAccount(CC_User ccUser, Configuration configuration, RequestsAuditService requestService,
			String accountId, Map<String, AccountDTO> accountsMap, APIAudits audit, String modifiedSince) {
		this.ccUser = ccUser;
		this.configuration = configuration;
		this.accountId = accountId;
		this.requestService = requestService;
		this.accountsMap = accountsMap;
		this.audit = audit;
		this.modifiedSince = modifiedSince;
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
		URI uri = null;
		ResponseEntity<String> response = null;
		List<Device> deviceDTOList = new ArrayList<Device>();
		JSONObject params = new JSONObject();
		try {
			do {
				uri = UriComponentsBuilder.fromUriString(url).queryParam("pageNumber", String.valueOf(pageNumber))
						.queryParam("modifiedSince",
								URLEncoder.encode(modifiedSince, StandardCharsets.UTF_8.toString()))
						.queryParam("accountId", accountId).build(true).toUri();

				params.put("pageNumber", pageNumber);
				params.put("modifiedSince", modifiedSince);
				params.put("accountId", accountId);

				response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
				if (response.getStatusCode() == HttpStatus.OK) {
					audit.doAudit("search Devices",
							configuration.getBaseUrl() + ControlCentreConstants.DEVICES_URL, null, params.toString(),
							ControlCentreConstants.STATUS_SUCCESS, ccUser, requestService);
					JSONObject deviceObject = new JSONObject(response.getBody().toString());
					lastPage = deviceObject.getBoolean("lastPage");
					JSONArray devicesArray = deviceObject.getJSONArray("devices");
					for (int i = 0; i < devicesArray.length(); i++) {
						Device deviceObj = new Device();
						deviceObj.setIccid(devicesArray.getJSONObject(i).getString("iccid"));
						deviceDTOList.add(deviceObj);
					}
					pageNumber++;
				}
			} while (!lastPage);
		} catch (Exception e) {
			logger.error(e);
			audit.doAudit("search Devices", configuration.getBaseUrl() + ControlCentreConstants.DEVICES_URL,
					e.getMessage(), params.toString(), ControlCentreConstants.STATUS_FAIL, ccUser, requestService);
			throw e;
		}
		accountsMap.get(accountId).setDeviceList(deviceDTOList);
		return null;
	}

}
