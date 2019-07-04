package com.psl.cc.analytics.controller;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.psl.cc.analytics.util.RootControllerUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RootControllerTest {

	private final String BASE_URL = "/api/v1";

	@Autowired
	private RootControllerUtil utils;

	@Test
	public void getAdminNames() {
		utils.setup();
	}

	@Test
	public void getAdminStats() {
		utils.tearDown();
	}
}
