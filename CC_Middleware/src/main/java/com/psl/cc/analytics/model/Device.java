package com.psl.cc.analytics.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonInclude(Include.NON_EMPTY)
@Document(collection = "device")
public class Device extends Audit {

	@Id
	private String iccId;
	@DBRef
	private AccountDTO account;
	private String status;
	private CommunicationPlanDetails commPlanDetails;
	private RatePlanDTO ratePlan;
	private Date dateActivated;
	private Date dateAdded;
	private Date dateUpdated;
	private Date dateShipped;
	private Date inventoryDate;
	private Date activationReadyDate;
	public String getIccId() {
		return iccId;
	}
	public void setIccId(String iccId) {
		this.iccId = iccId;
	}
	public AccountDTO getAccount() {
		return account;
	}
	public void setAccount(AccountDTO account) {
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
	public RatePlanDTO getRatePlan() {
		return ratePlan;
	}
	public void setRatePlan(RatePlanDTO ratePlan) {
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
	private Date deactivatedDate;
	private Date retiredDate;
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Device [");
		if (iccId != null) {
			builder.append("iccId=");
			builder.append(iccId);
			builder.append(", ");
		}
		if (account != null) {
			builder.append("account=");
			builder.append(account);
			builder.append(", ");
		}
		if (status != null) {
			builder.append("status=");
			builder.append(status);
			builder.append(", ");
		}
		if (commPlanDetails != null) {
			builder.append("commPlanDetails=");
			builder.append(commPlanDetails);
			builder.append(", ");
		}
		if (ratePlan != null) {
			builder.append("ratePlan=");
			builder.append(ratePlan);
			builder.append(", ");
		}
		if (dateActivated != null) {
			builder.append("dateActivated=");
			builder.append(dateActivated);
			builder.append(", ");
		}
		if (dateAdded != null) {
			builder.append("dateAdded=");
			builder.append(dateAdded);
			builder.append(", ");
		}
		if (dateUpdated != null) {
			builder.append("dateUpdated=");
			builder.append(dateUpdated);
			builder.append(", ");
		}
		if (dateShipped != null) {
			builder.append("dateShipped=");
			builder.append(dateShipped);
			builder.append(", ");
		}
		if (inventoryDate != null) {
			builder.append("inventoryDate=");
			builder.append(inventoryDate);
			builder.append(", ");
		}
		if (activationReadyDate != null) {
			builder.append("activationReadyDate=");
			builder.append(activationReadyDate);
			builder.append(", ");
		}
		if (deactivatedDate != null) {
			builder.append("deactivatedDate=");
			builder.append(deactivatedDate);
			builder.append(", ");
		}
		if (retiredDate != null) {
			builder.append("retiredDate=");
			builder.append(retiredDate);
		}
		builder.append("]");
		return builder.toString();
	}

	
}
