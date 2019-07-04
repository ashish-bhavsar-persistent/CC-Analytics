package com.psl.cc.analytics.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class AccountAggregation {

	private long total;
	private long monthlyTotal;
	private long yearlyTotal;

	private String ratePlan;
	private String communicationPlan;
	private String id;
	private String status;

	public String getId() {
		return id;
	}

	public long getMonthlyTotal() {
		return monthlyTotal;
	}

//	public void setMonthlyTotal(long monthlyTotal) {
//		this.monthlyTotal = monthlyTotal;
//	}

	public long getYearlyTotal() {
		return yearlyTotal;
	}

//	public void setYearlyTotal(long yearlyTotal) {
//		this.yearlyTotal = yearlyTotal;
//	}

//	public void setId(String id) {
//		this.id = id;
//	}

	public long getTotal() {
		return total;
	}

	public String getStatus() {
		return status;
	}

//	public void setStatus(String status) {
//		this.status = status;
//	}

//	public void setTotal(long total) {
//		this.total = total;
//	}

	public String getRatePlan() {
		return ratePlan;
	}

//	public void setRatePlan(String ratePlan) {
//		this.ratePlan = ratePlan;
//	}

	public String getCommunicationPlan() {
		return communicationPlan;
	}

//	public void setCommunicationPlan(String communicationPlan) {
//		this.communicationPlan = communicationPlan;
//	}

}
