package com.psl.cc.analytics.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.bouncycastle.util.encoders.Base64;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.psl.cc.analytics.util.AccountDetailsUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeviceControllerTest {

	private final String BASE_URL = "/api/v1";

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private AccountDetailsUtil utils;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void getRatePlanCount() throws Exception {
		String accessToken = getAccessToken("VivoSpAdmin", "password");
		mockMvc.perform(get(BASE_URL + "/devices/ratePlan").header("Authorization", "Bearer " + accessToken)
				.param("accountId", "100007512")).andExpect(status().isOk()).andExpect(jsonPath("$[0].total", is(2)))
				.andExpect(jsonPath("$[0].ratePlan", is("Vivo Default RP")));
	}

	@Test
	public void getCommPlanCount() throws Exception {
		utils.setup();
		String accessToken = getAccessToken("VivoSpAdmin", "password");
		mockMvc.perform(get(BASE_URL + "/devices/commPlan").header("Authorization", "Bearer " + accessToken)
				.param("accountId", "100007512")).andExpect(status().isOk()).andExpect(jsonPath("$[0].total", is(2)))
				.andExpect(jsonPath("$[0].communicationPlan", is("Vivo Default CP")));
	}

	@Test
	public void getStatusCount_WithoutConf() throws Exception {

		String accessToken = getAccessToken("Test", "password");
		mockMvc.perform(get(BASE_URL + "/devices/status").header("Authorization", "Bearer " + accessToken)
				.param("accountId", "100007512").param("granularity", "monthly")).andExpect(status().isOk());
	}
	
	@Test
	public void getStatusCount_Monthly() throws Exception {

		String accessToken = getAccessToken("VivoSpAdmin", "password");
		mockMvc.perform(get(BASE_URL + "/devices/status").header("Authorization", "Bearer " + accessToken)
				.param("accountId", "100007512").param("granularity", "monthly")).andExpect(status().isOk());
	}

	@Test
	public void getStatusCount_Yearly() throws Exception {

		String accessToken = getAccessToken("VivoSpAdmin", "password");
		mockMvc.perform(get(BASE_URL + "/devices/status").header("Authorization", "Bearer " + accessToken)
				.param("accountId", "100007512").param("granularity", "yearly")).andExpect(status().isOk());
		utils.tearDown();
	}

	private String getAccessToken(String username, String password) throws Exception {
		String authorization = "Basic " + new String(Base64.encode("ashish:secret".getBytes()));
		String contentType = MediaType.APPLICATION_JSON + ";charset=UTF-8";

		String content = mockMvc
				.perform(post("/oauth/token").header("Authorization", authorization)
						.contentType(MediaType.APPLICATION_FORM_URLENCODED).param("username", username)
						.param("password", password).param("grant_type", "password").param("scope", "read write"))
				.andExpect(status().isOk()).andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$.access_token", is(notNullValue())))
				.andExpect(jsonPath("$.token_type", is(equalTo("bearer"))))
				.andExpect(jsonPath("$.refresh_token", is(notNullValue())))
				.andExpect(jsonPath("$.expires_in", is(greaterThan(4000))))
				.andExpect(jsonPath("$.scope", is(equalTo("read write")))).andReturn().getResponse()
				.getContentAsString();
		return new JSONObject(content).getString("access_token");
	}

}
