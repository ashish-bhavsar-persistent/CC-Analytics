package com.psl.cc.analytics.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "device")
public class Device extends Audit {

	@Id
	private String iccId;
	@DBRef
	private Account account;
	private String status;
	private CommunicationPlanDetails commPlanDetails;
	private RatePlan ratePlan;
	private Date dateActivated;
	private Date dateAdded;
	private Date dateUpdated;
	private Date dateShipped;
	private Date inventoryDate;
	private Date activationReadyDate;
	private Date deactivatedDate;
	private Date retiredDate;

	public String getIccId() {
		return iccId;
	}

	public void setIccId(String iccId) {
		this.iccId = iccId;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
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

	public Date getDateActivated() {
		return dateActivated;
	}

	public void setDateActivated(Date dateActivated) {
		this.dateActivated = dateActivated;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public Date getDateShipped() {
		return dateShipped;
	}

	public void setDateShipped(Date dateShipped) {
		this.dateShipped = dateShipped;
	}

	public Date getInventoryDate() {
		return inventoryDate;
	}

	public void setInventoryDate(Date inventoryDate) {
		this.inventoryDate = inventoryDate;
	}

	public Date getActivationReadyDate() {
		return activationReadyDate;
	}

	public void setActivationReadyDate(Date activationReadyDate) {
		this.activationReadyDate = activationReadyDate;
	}

	public Date getDeactivatedDate() {
		return deactivatedDate;
	}

	public void setDeactivatedDate(Date deactivatedDate) {
		this.deactivatedDate = deactivatedDate;
	}

	public Date getRetiredDate() {
		return retiredDate;
	}

	public void setRetiredDate(Date retiredDate) {
		this.retiredDate = retiredDate;
	}

}
