package com.psl.cc.analytics.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.model.Device;
import com.psl.cc.analytics.repository.AccountsRepository;
import com.psl.cc.analytics.repository.ConfigurationRepository;
import com.psl.cc.analytics.response.AccountAggregation;
import com.psl.cc.analytics.service.AccountService;
import com.psl.cc.analytics.service.DeviceService;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private AccountsRepository repository;

	@Autowired
	private ConfigurationRepository configRepository;

	@Override
	public AccountDTO save(AccountDTO account) {
		return repository.save(account);

	}

	@Override
	public List<AccountDTO> getAllByUserId(String userId) {

		return repository.getAllByUserId(userId);
	}

	@Override
	public List<AccountDTO> getAllAccountNames(String userId) {
		return repository.getAllAccountNames(userId);
	}

	@Override
	public List<Device> getDeviceRatePlanOrCommCountPlanByAccountId(String userId, String accountId, String fieldName) {

		Optional<AccountDTO> optional = repository.findById(accountId);
		if (optional.isPresent() && (userId == null || userId.equals(optional.get().getUser().getId()))) {
			return deviceService.getDeviceRatePlanOrCommCountPlanByAccountId(accountId, fieldName);
		} else {
			return new ArrayList<>();
		}

	}

	@Override
	public List<Device> getDeviceStatusCountByAccountId(String userId, String accountId, String granularity) {
		Optional<AccountDTO> optional = repository.findById(accountId);
		Configuration conf = configRepository.findOneByUserId(userId);
		if (optional.isPresent() && (userId == null || userId.equals(optional.get().getUser().getId()))) {
			return deviceService.getDeviceStatusCountByAccountId(conf, accountId, granularity);
		} else {
			return new ArrayList<>();
		}
	}

	@Override
	public List<AccountAggregation> getAccountRatePlanOrCommCountPlan(String userId, String inputFieldName,
			String outputFieldName) {

		List<AggregationOperation> list = new ArrayList<>();
		list.add(Aggregation.match(Criteria.where("user.$id").is(new ObjectId(userId))));
		list.add(Aggregation.match(Criteria.where("status").regex("Active", "i")));

		list.add(Aggregation.group(inputFieldName).count().as("total"));
		list.add(Aggregation.project("total").and(outputFieldName).previousOperation());
		list.add(Aggregation.sort(Sort.Direction.ASC, "total"));

		TypedAggregation<AccountDTO> agg = Aggregation.newAggregation(AccountDTO.class, list);
		AggregationResults<AccountAggregation> account = mongoTemplate.aggregate(agg, AccountAggregation.class);
		return account.getMappedResults();

	}

	@Override
	public List<Device> getDeviceStatusCountByUserId(String userId) {

		List<AccountDTO> accountDTOs = repository.getAllAccountNames(userId);
		List<String> accountIds = new ArrayList<>();
		accountDTOs.forEach(accountDTO -> accountIds.add(accountDTO.getAccountId()));
		Configuration conf = configRepository.findOneByUserId(userId);
		return deviceService.getDeviceStatusCountByUserId(conf, accountIds);
//		Configuration conf = configRepository.findOneByUserId(userId);
//		if (conf != null) {
//			List<Pattern> regex = new ArrayList<>();
//
//			for (String status : conf.getDeviceStates()) {
//				regex.add(Pattern.compile(status, Pattern.CASE_INSENSITIVE));
//			}
//
//			List<AggregationOperation> list = new ArrayList<>();
//			list.add(Aggregation.unwind("deviceList"));
//			list.add(Aggregation.match(Criteria.where("user.$id").is(new ObjectId(userId))));
//			list.add(Aggregation.match(Criteria.where("deviceList.status").in(regex)));
//
//			list.add(Aggregation.group("deviceList.status").count().as("total"));
//			list.add(Aggregation.project("total").and("status").previousOperation());
//			list.add(Aggregation.sort(Sort.Direction.ASC, "total"));
//
//			TypedAggregation<AccountDTO> agg = Aggregation.newAggregation(AccountDTO.class, list);
//			AggregationResults<AccountAggregation> account = mongoTemplate.aggregate(agg, AccountAggregation.class);
//			return account.getMappedResults();
//		} else {
//			return new ArrayList<>();
//		}
	}

	@Override
	public long getCount() {
		return repository.count();
	}

	/*
	 * @Override public long getAllDevicesCount() { List<AggregationOperation> list
	 * = new ArrayList<>(); list.add(Aggregation.unwind("deviceList"));
	 * list.add(Aggregation.count().as("total")); TypedAggregation<AccountDTO> agg =
	 * Aggregation.newAggregation(AccountDTO.class, list);
	 * AggregationResults<AccountAggregation> account = mongoTemplate.aggregate(agg,
	 * AccountAggregation.class); AccountAggregation accountAggregation =
	 * account.getUniqueMappedResult(); return accountAggregation.getTotal();
	 * 
	 * }
	 */

}
