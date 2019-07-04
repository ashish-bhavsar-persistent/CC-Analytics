package com.psl.cc.analytics.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.psl.cc.analytics.model.Device;

public interface DeviceRepository extends MongoRepository<Device, String> {

	@Query(value = "{'accountId':?0}")
	public List<Device> findAllByAccountId(String accountId);
	
	@Query(value = "{'iccid':?0}")
	public Optional<Device> findOneByIccid(String iccid);
}
