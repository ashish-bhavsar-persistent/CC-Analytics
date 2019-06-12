package com.psl.cc.analytics.model;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class CommunicationPlanDetails {
	
	@XmlElement(name = "defaultCommPlan")
	private String communicationplanName;
	
	@XmlElement(name = "defaultOnCommProfile")
	private String defaultOnCommProfile;
	
	@XmlElement(name="defaultOffCommProfile")
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CommunicationPlanDetails [");
		if (communicationplanName != null) {
			builder.append("communicationplanName=");
			builder.append(communicationplanName);
			builder.append(", ");
		}
		if (defaultOnCommProfile != null) {
			builder.append("defaultOnCommProfile=");
			builder.append(defaultOnCommProfile);
			builder.append(", ");
		}
		if (defaultOffCommProfile != null) {
			builder.append("defaultOffCommProfile=");
			builder.append(defaultOffCommProfile);
		}
		builder.append("]");
		return builder.toString();
	}
	
	
	

}
