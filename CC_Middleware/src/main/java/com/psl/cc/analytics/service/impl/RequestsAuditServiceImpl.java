package com.psl.cc.analytics.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.psl.cc.analytics.model.RequestsAudit;
import com.psl.cc.analytics.repository.RequestsAuditRepository;
import com.psl.cc.analytics.service.RequestsAuditService;

@Service
public class RequestsAuditServiceImpl implements RequestsAuditService {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	RequestsAuditRepository auditRepository;

	@Override
	public RequestsAudit getLatestRecord() {
		Query query = new Query();
		query.limit(1);
		query.with(new Sort(Sort.Direction.DESC, "lastUpdatedOn"));
		RequestsAudit audit = mongoTemplate.findOne(query, RequestsAudit.class);
		return audit;
	}

	@Override
	public RequestsAudit save(RequestsAudit requestsAudit) {
		return auditRepository.save(requestsAudit);
	}

}
