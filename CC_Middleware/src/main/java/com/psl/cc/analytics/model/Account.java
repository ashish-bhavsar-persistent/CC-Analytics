package com.psl.cc.analytics.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "account")
public class Account extends Audit {

	@Id
	private String id;
	@DBRef
	private CC_User user;
	private String accountName;
	private String type;
	private String status;
	private CommunicationPlanDetails commPlanDetails;
	private RatePlan ratePlan;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CC_User getUser() {
		return user;
	}

	public void setUser(CC_User user) {
		this.user = user;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
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

	public CommunicationPlanDetails getCommPlanDetails() {
		return commPlanDetails;
	}

	public void setCommPlanDetails(CommunicationPlanDetails commPlanDetails) {
		this.commPlanDetails = commPlanDetails;
	}

	public RatePlan getRatePlan() {
		return ratePlan;
	}

	public void setRatePlan(RatePlan ratePlan) {
		this.ratePlan = ratePlan;
	}

}
