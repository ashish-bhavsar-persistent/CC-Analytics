package com.psl.cc.analytics.model;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement
@JsonInclude(Include.NON_EMPTY)
@Document(collection = "device")
public class Device extends Audit {

	@Id
	private String id;
	private String ratePlan;
	private String euiccid;
	private String imsi;
	private String dateAdded;
	private String operatorCustom2;
	private String operatorCustom1;
	private String iccid;
	private String operatorCustom4;
	private String operatorCustom3;
	private String customerCustom1;
	private String dateShipped;
	private String customerCustom2;
	private String globalSimType;
	private String msisdn;
	private String customerCustom5;
	private String customerCustom3;
	private String customerCustom4;
	private String modemID;
	private String communicationPlan;
	private String fixedIPAddress;
	private String endConsumerId;
	private String operatorCustom5;
	private String deviceID;
	private String dateUpdated;
	private String accountCustom3;
	private String accountId;
	private String accountCustom2;
	private String accountCustom10;
	private String accountCustom5;
	private String accountCustom4;
	private String accountCustom1;
	private String imei;
	private String dateActivated;
	private String accountCustom7;
	private String accountCustom6;
	private String accountCustom9;
	private String status;
	private String customer;
	private String accountCustom8;
	private String simNotes;
	private long total;

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public String getRatePlan() {
		return ratePlan;
	}

	public void setRatePlan(String ratePlan) {
		this.ratePlan = ratePlan;
	}

	public String getEuiccid() {
		return euiccid;
	}

	public void setEuiccid(String euiccid) {
		this.euiccid = euiccid;
	}

	public String getImsi() {
		return imsi;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(String dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getOperatorCustom2() {
		return operatorCustom2;
	}

	public void setOperatorCustom2(String operatorCustom2) {
		this.operatorCustom2 = operatorCustom2;
	}

	public String getOperatorCustom1() {
		return operatorCustom1;
	}

	public void setOperatorCustom1(String operatorCustom1) {
		this.operatorCustom1 = operatorCustom1;
	}

	public String getIccid() {
		return iccid;
	}

	public void setIccid(String iccid) {
		this.iccid = iccid;
	}

	public String getOperatorCustom4() {
		return operatorCustom4;
	}

	public void setOperatorCustom4(String operatorCustom4) {
		this.operatorCustom4 = operatorCustom4;
	}

	public String getOperatorCustom3() {
		return operatorCustom3;
	}

	public void setOperatorCustom3(String operatorCustom3) {
		this.operatorCustom3 = operatorCustom3;
	}

	public String getCustomerCustom1() {
		return customerCustom1;
	}

	public void setCustomerCustom1(String customerCustom1) {
		this.customerCustom1 = customerCustom1;
	}

	public String getDateShipped() {
		return dateShipped;
	}

	public void setDateShipped(String dateShipped) {
		this.dateShipped = dateShipped;
	}

	public String getCustomerCustom2() {
		return customerCustom2;
	}

	public void setCustomerCustom2(String customerCustom2) {
		this.customerCustom2 = customerCustom2;
	}

	public String getGlobalSimType() {
		return globalSimType;
	}

	public void setGlobalSimType(String globalSimType) {
		this.globalSimType = globalSimType;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getCustomerCustom5() {
		return customerCustom5;
	}

	public void setCustomerCustom5(String customerCustom5) {
		this.customerCustom5 = customerCustom5;
	}

	public String getCustomerCustom3() {
		return customerCustom3;
	}

	public void setCustomerCustom3(String customerCustom3) {
		this.customerCustom3 = customerCustom3;
	}

	public String getCustomerCustom4() {
		return customerCustom4;
	}

	public void setCustomerCustom4(String customerCustom4) {
		this.customerCustom4 = customerCustom4;
	}

	public String getModemID() {
		return modemID;
	}

	public void setModemID(String modemID) {
		this.modemID = modemID;
	}

	public String getCommunicationPlan() {
		return communicationPlan;
	}

	public void setCommunicationPlan(String communicationPlan) {
		this.communicationPlan = communicationPlan;
	}

	public String getFixedIPAddress() {
		return fixedIPAddress;
	}

	public void setFixedIPAddress(String fixedIPAddress) {
		this.fixedIPAddress = fixedIPAddress;
	}

	public String getEndConsumerId() {
		return endConsumerId;
	}

	public void setEndConsumerId(String endConsumerId) {
		this.endConsumerId = endConsumerId;
	}

	public String getOperatorCustom5() {
		return operatorCustom5;
	}

	public void setOperatorCustom5(String operatorCustom5) {
		this.operatorCustom5 = operatorCustom5;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(String dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public String getAccountCustom3() {
		return accountCustom3;
	}

	public void setAccountCustom3(String accountCustom3) {
		this.accountCustom3 = accountCustom3;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getAccountCustom2() {
		return accountCustom2;
	}

	public void setAccountCustom2(String accountCustom2) {
		this.accountCustom2 = accountCustom2;
	}

	public String getAccountCustom10() {
		return accountCustom10;
	}

	public void setAccountCustom10(String accountCustom10) {
		this.accountCustom10 = accountCustom10;
	}

	public String getAccountCustom5() {
		return accountCustom5;
	}

	public void setAccountCustom5(String accountCustom5) {
		this.accountCustom5 = accountCustom5;
	}

	public String getAccountCustom4() {
		return accountCustom4;
	}

	public void setAccountCustom4(String accountCustom4) {
		this.accountCustom4 = accountCustom4;
	}

	public String getAccountCustom1() {
		return accountCustom1;
	}

	public void setAccountCustom1(String accountCustom1) {
		this.accountCustom1 = accountCustom1;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getDateActivated() {
		return dateActivated;
	}

	public void setDateActivated(String dateActivated) {
		this.dateActivated = dateActivated;
	}

	public String getAccountCustom7() {
		return accountCustom7;
	}

	public void setAccountCustom7(String accountCustom7) {
		this.accountCustom7 = accountCustom7;
	}

	public String getAccountCustom6() {
		return accountCustom6;
	}

	public void setAccountCustom6(String accountCustom6) {
		this.accountCustom6 = accountCustom6;
	}

	public String getAccountCustom9() {
		return accountCustom9;
	}

	public void setAccountCustom9(String accountCustom9) {
		this.accountCustom9 = accountCustom9;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getAccountCustom8() {
		return accountCustom8;
	}

	public void setAccountCustom8(String accountCustom8) {
		this.accountCustom8 = accountCustom8;
	}

	public String getSimNotes() {
		return simNotes;
	}

	public void setSimNotes(String simNotes) {
		this.simNotes = simNotes;
	}

}
