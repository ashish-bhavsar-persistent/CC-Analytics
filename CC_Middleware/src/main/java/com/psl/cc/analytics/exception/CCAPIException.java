package com.psl.cc.analytics.exception;

import com.psl.cc.analytics.model.CCUser;

public class CCAPIException extends RuntimeException {
	private static final long serialVersionUID = 4146985475187421381L;

	private final String apiName;
	private final String endpointUrl;
	private final String errorDetails;
	private final String params;
	private final String status;
	private final transient CCUser ccUser;

	public CCAPIException(String apiName, String endpointUrl, String errorDetails, String params, String status,
			CCUser ccUser) {
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

	public CCUser getCcUser() {
		return ccUser;
	}

}
