package com.psl.cc.analytics.service;

import com.psl.cc.analytics.model.RequestsAudit;

public interface RequestsAuditService {

	RequestsAudit getLatestRecord();

	RequestsAudit save(RequestsAudit requestsAudit);
}
