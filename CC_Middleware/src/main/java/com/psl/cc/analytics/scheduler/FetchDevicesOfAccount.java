package com.psl.cc.analytics.scheduler;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

import com.psl.cc.analytics.constants.ControlCentreConstants;
import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.model.CCUser;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.model.Device;
import com.psl.cc.analytics.service.RequestsAuditService;
import com.psl.cc.analytics.utils.APIAudits;

public class FetchDevicesOfAccount implements Callable<Optional<String>> {
	private static final Logger logger = LogManager.getLogger(FetchDevicesOfAccount.class);
	private final String accountId;
	private final CCUser ccUser;
	private final Configuration configuration;
	private final RequestsAuditService requestService;
	private final AccountDTO account;
	private final APIAudits audit;
	private final String modifiedSince;
	private final RestTemplate restTemplate;

	public FetchDevicesOfAccount(Configuration configuration, RequestsAuditService requestService, String accountId,
			AccountDTO account, APIAudits audit, String modifiedSince, RestTemplate restTemplate) {
		this.ccUser = account.getUser();
		this.accountId = accountId;
		this.requestService = requestService;
		this.account = account;
		this.audit = audit;
		this.modifiedSince = modifiedSince;
		this.configuration = configuration;
		this.restTemplate = restTemplate;
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
		final HttpEntity<String> request = new HttpEntity<>(headers);

		final String url = configuration.getBaseUrl() + ControlCentreConstants.DEVICES_URL;
		boolean lastPage = false;
		int pageNumber = 1;
		URI uri = null;
		ResponseEntity<String> response = null;
		List<Device> deviceDTOList = new ArrayList<>();
		JSONObject params = new JSONObject();
		try {
			do {
				uri = UriComponentsBuilder.fromUriString(url).queryParam("pageNumber", String.valueOf(pageNumber))
						.queryParam("modifiedSince",
								URLEncoder.encode(modifiedSince, StandardCharsets.UTF_8.toString()))
						.queryParam("accountId", accountId).queryParam("pageSize", ControlCentreConstants.PAGE_SIZE)
						.build(true).toUri();

				params.put("pageNumber", pageNumber);
				params.put("modifiedSince", modifiedSince);
				params.put("accountId", accountId);
				params.put("pageSize", ControlCentreConstants.PAGE_SIZE);

				response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
				if (response.getStatusCode() == HttpStatus.OK) {
					audit.doAudit("search Devices", configuration.getBaseUrl() + ControlCentreConstants.DEVICES_URL,
							null, params.toString(), ControlCentreConstants.STATUS_SUCCESS, ccUser, requestService);
					JSONObject deviceObject = new JSONObject(response.getBody());
					lastPage = deviceObject.getBoolean("lastPage");
					JSONArray devicesArray = deviceObject.getJSONArray("devices");

					devicesArray.forEach(deviceObj -> {
						Device devicedto = new Device();
						devicedto.setIccid(new JSONObject(deviceObj.toString()).getString("iccid"));
						devicedto.setCreatedOn(new Date());
						devicedto.setLastUpdatedOn(new Date());
						deviceDTOList.add(devicedto);
					});

					pageNumber++;
				}
			} while (!lastPage);
		} catch (Exception e) {
			logger.error(e);
			audit.doAudit("search Devices", configuration.getBaseUrl() + ControlCentreConstants.DEVICES_URL,
					e.getMessage(), params.toString(), ControlCentreConstants.STATUS_FAIL, ccUser, requestService);
			throw e;
		}
		logger.debug("Length of device array is {} for account number {}", deviceDTOList.size(), accountId);
		if (account.getDeviceList() == null) {
			account.setDeviceList(deviceDTOList);
		} else {
			deviceDTOList.forEach(device -> account.getDeviceList().add(device));
		}

		return Optional.ofNullable(null);
	}

}
