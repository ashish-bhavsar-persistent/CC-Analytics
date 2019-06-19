package com.psl.cc.analytics.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

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

import com.psl.cc.analytics.constants.ControlCentreConstants;
import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.repository.AccountsRepository;
import com.psl.cc.analytics.repository.ConfigurationRepository;
import com.psl.cc.analytics.response.AccountAggregation;
import com.psl.cc.analytics.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private AccountsRepository repository;

	@Autowired
	private ConfigurationRepository configRepository;

	@Override
	public List<AccountDTO> saveAll(Iterable<AccountDTO> accounts) {
		return repository.saveAll(accounts);

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
	public List<AccountAggregation> getRatePlanCountOrCommPlanByAccountId(String userId, String accountId,
			String filedName) {
		DateFormat format = new SimpleDateFormat(ControlCentreConstants.DATEFORMAT_DEVICES);
		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), 0, 1, 0, 0, 0);

		final String startDate = format.format(c.getTime());
		c.set(c.get(Calendar.YEAR), 11, 31, 0, 0, 0);
		final String endDate = format.format(c.getTime());

		List<AggregationOperation> list = new ArrayList<AggregationOperation>();
		list.add(Aggregation.unwind("deviceList"));
		list.add(Aggregation.match(Criteria.where("user.$id").is(new ObjectId(userId))));
		list.add(Aggregation.match(Criteria.where("accountId").is(accountId)));
		list.add(Aggregation.match(Criteria.where("deviceList.dateUpdated").gte(startDate).lte(endDate)));
		list.add(Aggregation.group("deviceList." + filedName).count().as("total"));
		list.add(Aggregation.project("total").and(filedName).previousOperation());
		list.add(Aggregation.sort(Sort.Direction.ASC, "total"));

		TypedAggregation<AccountDTO> agg = Aggregation.newAggregation(AccountDTO.class, list);
		AggregationResults<AccountAggregation> account = mongoTemplate.aggregate(agg, AccountAggregation.class);
		return account.getMappedResults();

	}

	@Override
	public List<AccountAggregation> getStatusCountByAccountId(String userId, String accountId, String granularity) {
		Configuration conf = configRepository.findOneByCC_UserId(userId);
		if (conf != null) {
			List<Pattern> regex = new ArrayList<Pattern>();
			int i = 0;
			for (String status : conf.getDeviceStates()) {
				regex.add(Pattern.compile(status, Pattern.CASE_INSENSITIVE));
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

			List<AggregationOperation> list = new ArrayList<AggregationOperation>();
			list.add(Aggregation.unwind("deviceList"));
			list.add(Aggregation.match(Criteria.where("user.$id").is(new ObjectId(userId))));
			list.add(Aggregation.match(Criteria.where("accountId").is(accountId)));
			list.add(Aggregation.match(Criteria.where("deviceList.status").in(regex)));
			list.add(Aggregation.match(Criteria.where("deviceList.dateUpdated").gte(startDate).lte(endDate)));
			list.add(Aggregation.group("deviceList.status").count().as("total"));
			list.add(Aggregation.project("total").and("status").previousOperation());
			list.add(Aggregation.sort(Sort.Direction.ASC, "total"));

			TypedAggregation<AccountDTO> agg = Aggregation.newAggregation(AccountDTO.class, list);
			AggregationResults<AccountAggregation> account = mongoTemplate.aggregate(agg, AccountAggregation.class);
			return account.getMappedResults();
		} else {
			return null;
		}
	}

}
