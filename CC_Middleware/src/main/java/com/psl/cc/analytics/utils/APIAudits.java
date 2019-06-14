package com.psl.cc.analytics.utils;

import java.util.Date;

import com.psl.cc.analytics.model.CC_User;
import com.psl.cc.analytics.model.RequestsAudit;
import com.psl.cc.analytics.service.RequestsAuditService;

public class APIAudits {

	public void doAudit(String apiName, String endpointUrl, String errorDetails, String params, String status,
			CC_User ccUser, RequestsAuditService requestService) {
		RequestsAudit audit = new RequestsAudit();
		audit.setApiName(apiName);
		audit.setCreatedOn(new Date());
		audit.setEndpointUrl(endpointUrl);
		audit.setErrorDetails(errorDetails);
		audit.setLastUpdatedOn(new Date());
		audit.setStatus(status);
		audit.setUser(ccUser);
		audit.setParams(params);
		requestService.save(audit);
	}
}
