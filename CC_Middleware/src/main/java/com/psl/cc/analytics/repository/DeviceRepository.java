package com.psl.cc.analytics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.psl.cc.analytics.model.Device;

public interface DeviceRepository extends MongoRepository<Device, String> {

}
