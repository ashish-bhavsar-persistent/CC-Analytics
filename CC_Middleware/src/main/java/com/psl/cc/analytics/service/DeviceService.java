package com.psl.cc.analytics.service;

import java.util.List;

import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.model.Device;

public interface DeviceService {

	public List<Device> saveAll(Iterable<Device> devices);

	public List<Device> findAllByAccountId(String accountId);

	public long getCount();

	public List<Device> getDeviceRatePlanOrCommCountPlanByAccountId(String accountId, String fieldName);

	public List<Device> getDeviceStatusCountByUserId(Configuration conf, List<String> accountIds);
	
	public List<Device> getDeviceStatusCountByAccountId(Configuration conf, String accountId, String granularity);
}
