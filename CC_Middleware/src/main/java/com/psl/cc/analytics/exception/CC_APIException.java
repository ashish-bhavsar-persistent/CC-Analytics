package com.psl.cc.analytics.exception;

import com.psl.cc.analytics.model.CC_User;

public class CC_APIException extends RuntimeException {
	private static final long serialVersionUID = 4146985475187421381L;

	private String apiName;
	private String endpointUrl;
	private String errorDetails;
	private String params;
	private String status;
	private CC_User ccUser;

	public CC_APIException(String apiName, String endpointUrl, String errorDetails, String params, String status,
			CC_User ccUser) {
		super(errorDetails);
		this.apiName = apiName;
		this.endpointUrl = endpointUrl;
		this.errorDetails = errorDetails;
		this.params = params;
		this.status = status;
		this.ccUser = ccUser;
	}

	public String getApiName() {
		return apiName;
	}

	public String getEndpointUrl() {
		return endpointUrl;
	}

	public String getErrorDetails() {
		return errorDetails;
	}

	public String getParams() {
		return params;
	}

	public String getStatus() {
		return status;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public CC_User getCcUser() {
		return ccUser;
	}

}
