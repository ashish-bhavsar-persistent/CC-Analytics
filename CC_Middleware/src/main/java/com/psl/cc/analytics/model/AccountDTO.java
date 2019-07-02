package com.psl.cc.analytics.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement
@JsonInclude(Include.NON_EMPTY)
@Document(collection = "account")
public class AccountDTO extends Audit {

	@Id
	private String accountId;
	private String accountName;
	private String type;
	private String status;
	private String currency;
	private String operatorAccountId;
	private String taxId;
	private CommunicationPlanDetailsDTO commPlanDetails;
	private RatePlanDTO defaultRatePlan;
	private List<Device> deviceList;

	@DBRef
	private CCUser user;

	public CCUser getUser() {
		return user;
	}

	public void setUser(CCUser user) {
		this.user = user;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getOperatorAccountId() {
		return operatorAccountId;
	}

	public void setOperatorAccountId(String operatorAccountId) {
		this.operatorAccountId = operatorAccountId;
	}

	public String getTaxId() {
		return taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	public CommunicationPlanDetailsDTO getCommPlanDetails() {
		return commPlanDetails;
	}

	public void setCommPlanDetails(CommunicationPlanDetailsDTO commPlanDetails) {
		this.commPlanDetails = commPlanDetails;
	}

	public RatePlanDTO getDefaultRatePlan() {
		return defaultRatePlan;
	}

	public void setDefaultRatePlan(RatePlanDTO defaultRatePlan) {
		this.defaultRatePlan = defaultRatePlan;
	}

	public List<Device> getDeviceList() {
		return deviceList;
	}

	public void setDeviceList(List<Device> deviceList) {
		this.deviceList = deviceList;
	}

}
