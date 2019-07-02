package com.psl.cc.analytics.model;

public class CommunicationPlanDetailsDTO {

	private String defaultCommPlan;
	private String defaultOnCommProfile;
	private String defaultOffCommProfile;
	private String onCommProfileHlrTemplateId;
	private String offCommProfileHlrTemplateId;

	public void setDefaultCommPlan(String defaultCommPlan) {
		this.defaultCommPlan = defaultCommPlan;
	}

	public void setDefaultOnCommProfile(String defaultOnCommProfile) {
		this.defaultOnCommProfile = defaultOnCommProfile;
	}

	public void setDefaultOffCommProfile(String defaultOffCommProfile) {
		this.defaultOffCommProfile = defaultOffCommProfile;
	}

	public void setOnCommProfileHlrTemplateId(String onCommProfileHlrTemplateId) {
		this.onCommProfileHlrTemplateId = onCommProfileHlrTemplateId;
	}

	public void setOffCommProfileHlrTemplateId(String offCommProfileHlrTemplateId) {
		this.offCommProfileHlrTemplateId = offCommProfileHlrTemplateId;
	}

}
