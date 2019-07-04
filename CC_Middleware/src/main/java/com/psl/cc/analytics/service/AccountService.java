package com.psl.cc.analytics.service;

import java.util.Collection;
import java.util.List;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.security.core.GrantedAuthority;

import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.response.AccountAggregation;

public interface AccountService {

	public AccountDTO save(AccountDTO account);

	public List<AccountDTO> getAllByUserId(String userId);

	public List<AccountDTO> getAllAccountNames(String userId);

	public List<AccountAggregation> getDeviceRatePlanOrCommCountPlanByAccountId(String userId, String accountId,
			String fieldName);

	public List<AccountAggregation> getDeviceStatusCountByAccountId(String userId, String accountId,
			String granularity);

	public List<AccountAggregation> getAccountRatePlanOrCommCountPlan(String userId, String inputFieldName,
			String outputFieldName);

	public List<AccountAggregation> getDeviceStatusCountByUserId(String userId);

	public long getAllAccountsCount();

	public long getAllDevicesCount();
}
