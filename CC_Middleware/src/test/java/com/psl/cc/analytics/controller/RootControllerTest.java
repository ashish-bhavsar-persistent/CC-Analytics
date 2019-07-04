package com.psl.cc.analytics.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
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

import com.psl.cc.analytics.util.RootControllerUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RootControllerTest {

	private final String BASE_URL = "/api/v1";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private RootControllerUtil utils;

	@Test
	public void getAdminNames() throws Exception {
		utils.setup();
		String accessToken = getAccessToken("cc_sysadmin", "password");
		mockMvc.perform(get(BASE_URL + "/admins/name").header("Authorization", "Bearer " + accessToken)
				.param("accountId", "100007512")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].username", is("VivoSpAdmin")));

	}

	@Test
	public void getAdminStats() throws Exception {
		String accessToken = getAccessToken("cc_sysadmin", "password");
		mockMvc.perform(get(BASE_URL + "/admins/stats").header("Authorization", "Bearer " + accessToken)
				.param("accountId", "100007512")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].label", is("Service Provider")))
				.andExpect(jsonPath("$[0].count", is(1)))
				.andExpect(jsonPath("$[1].label", is("Accounts")))
				.andExpect(jsonPath("$[1].count", is(0)));
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
