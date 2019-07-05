package com.psl.cc.analytics.scheduler;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.psl.cc.analytics.util.SchedulerUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SchedulerTest {

	@Autowired
	private GetAllAccounts accounts;

	@Autowired
	private SchedulerUtils util;

	@Test
	public void initializeFirstTime() throws Exception {
		util.setup();
		accounts.initializeFirstTime();

	}

	@Test
	public void cronJob() throws Exception {
		util.setup();
		accounts.cronJob();
		util.tearDown();
	}

	@Test
	public void initializeFirstTime_ReuseData() throws Exception {
		accounts.initializeFirstTime();
		util.tearDown();
	}
}
