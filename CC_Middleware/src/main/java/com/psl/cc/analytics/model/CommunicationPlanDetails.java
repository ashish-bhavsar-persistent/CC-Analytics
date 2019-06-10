package com.psl.cc.analytics.model;

public class CommunicationPlanDetails {
	private String communicationplanName;
	private String defaultOnCommProfile;
	private String defaultOffCommProfile;

	public String getCommunicationplanName() {
		return communicationplanName;
	}

	public void setCommunicationplanName(String communicationplanName) {
		this.communicationplanName = communicationplanName;
	}

	public String getDefaultOnCommProfile() {
		return defaultOnCommProfile;
	}

	public void setDefaultOnCommProfile(String defaultOnCommProfile) {
		this.defaultOnCommProfile = defaultOnCommProfile;
	}

	public String getDefaultOffCommProfile() {
		return defaultOffCommProfile;
	}

	public void setDefaultOffCommProfile(String defaultOffCommProfile) {
		this.defaultOffCommProfile = defaultOffCommProfile;
	}

}
