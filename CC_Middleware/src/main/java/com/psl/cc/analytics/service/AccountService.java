package com.psl.cc.analytics.service;

import java.util.List;

import com.psl.cc.analytics.model.AccountDTO;

public interface AccountService {

	public List<AccountDTO> saveAll(Iterable<AccountDTO> accounts);

	public List<AccountDTO> getAllByUserId(String userId);

	public List<AccountDTO> getAllAccountNames(String userId);

}
