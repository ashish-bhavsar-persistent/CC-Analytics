package com.psl.cc.analytics.scheduler;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.psl.cc.analytics.model.CC_User;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.model.RequestsAudit;
import com.psl.cc.analytics.repository.ConfigurationRepository;
import com.psl.cc.analytics.service.RequestsAuditService;
import com.psl.cc.analytics.service.UserService;

@Component
public class GetAllAccounts {

	@Autowired
	private RequestsAuditService requestService;

	@Autowired
	private ConfigurationRepository configRepository;

	@Autowired
	private UserService userService;

	// It shoud be run at 12:00:00 am everyday
	@Scheduled(cron = "0 0 0 * * * ?")
	public void cronJob() {
		getAllAccounts();
	}

	public void initializeFirstTime() {
		if (requestService.getLatestRecord() == null) {
			System.out.println("Initializing First Time at " + new Date());
			getAllAccounts();
		}
	}

	private void getAllAccounts() {
		List<CC_User> cc_users = userService.findAll();
		for (CC_User cc_user : cc_users) {
			Configuration configuration = configRepository.findOneByCC_UserId(cc_user.getId());
			if (configuration != null) {
				Runnable r = new FetchAccountDetails(cc_user, configuration);
				Thread thread = new Thread(r);
				thread.start();
			}
		}
	}

	class FetchAccountDetails implements Runnable {

		private CC_User cc_user;
		private Configuration configuration;

		public FetchAccountDetails(CC_User cc_user, Configuration configuration) {
			this.cc_user = cc_user;
			this.configuration = configuration;
		}

		@Override
		public void run() {
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
				URI url = new URI(configuration.getBaseUrl() + "/api/v1/accounts");
				ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
				if (response.getStatusCode() == HttpStatus.OK) {
					System.out.println(response.getBody().toString());
					doAudit("getAllAccounts", configuration.getBaseUrl() + "/api/v1/accounts", null, "Success");
					JSONObject accountsObject = new JSONObject(response.getBody().toString());
					
				}
			} catch (Exception e) {
				doAudit("getAllAccounts", configuration.getBaseUrl() + "/api/v1/accounts", e.getMessage(), "Error");
				e.printStackTrace();
			}

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
}