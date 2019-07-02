package com.psl.cc.analytics.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "config")

public class Configuration extends Audit {
	@Id
	private String id;
	@DBRef
	private CCUser user;
	private String baseUrl;
	private int billingCycleStartDay;
	private int billingCyclePeriod;
	private List<String> deviceStates;
	private boolean usePassword;
	private boolean useAPIKey;
	private String apiKey;

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setUser(CCUser user) {
		this.user = user;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public int getBillingCycleStartDay() {
		return billingCycleStartDay;
	}

	public void setBillingCycleStartDay(int billingCycleStartDay) {
		this.billingCycleStartDay = billingCycleStartDay;
	}

	public int getBillingCyclePeriod() {
		return billingCyclePeriod;
	}

	public void setBillingCyclePeriod(int billingCyclePeriod) {
		this.billingCyclePeriod = billingCyclePeriod;
	}

	public List<String> getDeviceStates() {
		return deviceStates;
	}

	public void setDeviceStates(List<String> deviceStates) {
		this.deviceStates = deviceStates;
	}

	public boolean isUsePassword() {
		return usePassword;
	}

	public void setUsePassword(boolean usePassword) {
		this.usePassword = usePassword;
	}

	public boolean isUseAPIKey() {
		return useAPIKey;
	}

	public void setUseAPIKey(boolean useAPIKey) {
		this.useAPIKey = useAPIKey;
	}

	public Configuration(CCUser user, String baseUrl, int billingCycleStartDay, int billingCyclePeriod,
			List<String> deviceStates, boolean usePassword, boolean useAPIKey, String apiKey) {
		super();

		this.user = user;
		this.baseUrl = baseUrl;
		this.billingCycleStartDay = billingCycleStartDay;
		this.billingCyclePeriod = billingCyclePeriod;
		this.deviceStates = deviceStates;
		this.usePassword = usePassword;
		this.useAPIKey = useAPIKey;
		this.apiKey = apiKey;
	}

	public Configuration() {
		super();
	}

}
