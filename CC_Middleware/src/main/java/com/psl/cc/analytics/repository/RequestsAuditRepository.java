package com.psl.cc.analytics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.psl.cc.analytics.model.RequestsAudit;

public interface RequestsAuditRepository extends MongoRepository<RequestsAudit, String> {

}
