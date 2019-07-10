package com.psl.cc.analytics.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.psl.cc.analytics.constants.ControlCentreConstants;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.model.Device;
import com.psl.cc.analytics.repository.DeviceRepository;
import com.psl.cc.analytics.service.DeviceService;

@Service
public class DeviceServiceImpl implements DeviceService {

	@Autowired
	private DeviceRepository repository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<Device> saveAll(Iterable<Device> devices) {
		return repository.saveAll(devices);
	}

	@Override
	public List<Device> findAllByAccountId(String accountId) {
		return repository.findAllByAccountId(accountId);
	}

	@Override
	public long getCount() {
		return repository.count();
	}

	public Optional<Device> findOneByIccid(String iccid) {
		return repository.findOneByIccid(iccid);
	}

	@Override
	public List<Device> getDeviceStatusCountByUserId(Configuration conf, List<String> accountIds) {

		if (conf != null) {
			List<Pattern> regex = new ArrayList<>();

			for (String status : conf.getDeviceStates()) {
				regex.add(Pattern.compile(status, Pattern.CASE_INSENSITIVE));
			}

			List<AggregationOperation> list = new ArrayList<>();

			list.add(Aggregation.match(Criteria.where("accountId").in(accountIds)));
			list.add(Aggregation.match(Criteria.where("status").in(regex)));

			list.add(Aggregation.group("status").count().as("total"));
			list.add(Aggregation.project("total").and("status").previousOperation());
			list.add(Aggregation.sort(Sort.Direction.ASC, "total"));

			TypedAggregation<Device> agg = Aggregation.newAggregation(Device.class, list);
			AggregationResults<Device> account = mongoTemplate.aggregate(agg, Device.class);
			return account.getMappedResults();
		} else {
			return new ArrayList<>();
		}
	}

	public List<Device> getDeviceRatePlanOrCommCountPlanByAccountId(String accountId, String fieldName) {

		DateFormat format = new SimpleDateFormat(ControlCentreConstants.DATEFORMAT_DEVICES);

		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), 0, 1, 0, 0, 0);

		final String startDate = format.format(c.getTime());
		c.set(c.get(Calendar.YEAR), 11, 31, 0, 0, 0);
		final String endDate = format.format(c.getTime());

		List<AggregationOperation> list = new ArrayList<>();

		list.add(Aggregation.match(Criteria.where("accountId").is(accountId)));
		list.add(Aggregation.match(Criteria.where("dateUpdated").gte(startDate).lte(endDate)));
		list.add(Aggregation.group(fieldName).count().as("total"));
		list.add(Aggregation.project("total").and(fieldName).previousOperation());
		list.add(Aggregation.sort(Sort.Direction.ASC, "total"));

		TypedAggregation<Device> agg = Aggregation.newAggregation(Device.class, list);
		AggregationResults<Device> account = mongoTemplate.aggregate(agg, Device.class);
		return account.getMappedResults();

	}

	public List<Device> getDeviceStatusCountByAccountId(Configuration conf, String accountId, String granularity) {

		List<Pattern> regex = null;
		if (conf != null) {
			regex = new ArrayList<>();
			for (String status : conf.getDeviceStates()) {
				regex.add(Pattern.compile(status, Pattern.CASE_INSENSITIVE));
			}
		}
		DateFormat format = new SimpleDateFormat(ControlCentreConstants.DATEFORMAT_DEVICES);
		Calendar c = Calendar.getInstance();
		final String startDate;
		final String endDate;
		if (!granularity.equalsIgnoreCase("monthly")) {
			c.set(c.get(Calendar.YEAR), 0, 1, 0, 0, 0);
			startDate = format.format(c.getTime());
			c.set(c.get(Calendar.YEAR), 11, 31, 0, 0, 0);
			endDate = format.format(c.getTime());
		} else {
			c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1, 0, 0, 0);
			startDate = format.format(c.getTime());
			c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.getActualMaximum(Calendar.DAY_OF_MONTH), 0, 0, 0);
			endDate = format.format(c.getTime());
		}

		List<AggregationOperation> list = new ArrayList<>();
		list.add(Aggregation.match(Criteria.where("accountId").is(accountId)));
		if (regex != null) {
			list.add(Aggregation.match(Criteria.where("status").in(regex)));
		}
		list.add(Aggregation.match(Criteria.where("dateUpdated").gte(startDate).lte(endDate)));
		list.add(Aggregation.group("status").count().as("total"));
		list.add(Aggregation.project("total").and("status").previousOperation());
		list.add(Aggregation.sort(Sort.Direction.ASC, "total"));

		TypedAggregation<Device> agg = Aggregation.newAggregation(Device.class, list);
		AggregationResults<Device> account = mongoTemplate.aggregate(agg, Device.class);
		return account.getMappedResults();

	}

	@Override
	public Device save(Device device) {
		return repository.save(device);
	}

	@Override
	public long getDeviceCountByUserId(List<String> accountIds) {
		List<AggregationOperation> list = new ArrayList<>();
		list.add(Aggregation.match(Criteria.where("accountId").in(accountIds)));
		list.add(Aggregation.count().as("total"));
		TypedAggregation<Device> agg = Aggregation.newAggregation(Device.class, list);
		AggregationResults<Device> account = mongoTemplate.aggregate(agg, Device.class);
		return account.getUniqueMappedResult().getTotal();
	}
}
