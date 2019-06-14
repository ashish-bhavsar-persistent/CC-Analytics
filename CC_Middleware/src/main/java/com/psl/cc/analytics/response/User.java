package com.psl.cc.analytics.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.psl.cc.analytics.model.CC_User;
import com.psl.cc.analytics.model.Configuration;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)

//@JsonInclude(Include.NON_EMPTY)
public class User implements Serializable {

	private static final long serialVersionUID = 1996838027864776402L;

	private String id;

	@NotNull(message = "Please provide Name")
	@NotEmpty(message = "Please provide Name")
	private String name;

	@NotNull(message = "Please provide apiKey")
	@NotEmpty(message = "Please provide apiKey")
	private String apiKey;

	@NotNull(message = "Please provide Name")
	@NotEmpty(message = "Please provide Name")
	private String username;

	@NotNull(message = "Please provide Password")
	@NotEmpty(message = "Please provide Password")
	private String password;

	@NotNull(message = "Please provide Roles")
	@NotEmpty(message = "Please provide Roles")
	private List<String> roles;

	@NotNull(message = "Please provide BaseUrl")
	@NotEmpty(message = "Please provide BaseUrl")
	private String baseUrl;

	@Min(value = 1, message = "BillingCycleStartDay greater than 0")
	@Max(value = 31, message = "BillingCycleStartDay less than or equals to 31")
	private int billingCycleStartDay;

	@Min(value = 1, message = "BillingCyclePeriod greater than 0")
	@Max(value = 31, message = "BillingCyclePeriod less than or equals to 31")
	private int billingCyclePeriod;

	@NotNull(message = "Please provide DeviceStates")
	@NotEmpty(message = "Please provide DeviceStates")
	private List<String> deviceStates;

	private boolean usePassword;
	private boolean useAPIKey;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getId() {
		return id;
	}

//	public void setId(String id) {
//		this.id = id;
//	}

	public String getName() {
		return name;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
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

	public User(CC_User user, Configuration config) {
		id = user.getId();
		username = user.getUsername();
		password = null;
		roles = new ArrayList<String>();
		name = user.getName();
		user.getRoles().forEach(r -> roles.add(r.getRole()));
		if (config != null) {
			baseUrl = config.getBaseUrl();
			billingCycleStartDay = config.getBillingCycleStartDay();
			billingCyclePeriod = config.getBillingCyclePeriod();
			deviceStates = new ArrayList<String>(config.getDeviceStates());
			usePassword = config.isUsePassword();
			useAPIKey = config.isUseAPIKey();
			apiKey = config.getApiKey();
		}
	}

	public User() {
		super();
	}

}
