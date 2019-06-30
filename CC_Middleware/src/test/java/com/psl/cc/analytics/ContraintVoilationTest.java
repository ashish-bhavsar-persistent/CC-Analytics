package com.psl.cc.analytics;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.bouncycastle.util.encoders.Base64;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.psl.cc.analytics.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ContraintVoilationTest {
	
	private static final String BASE_URL = "/api/v1";
	
	@Autowired
	WebApplicationContext context;

	@Autowired
	private MockMvc mockMvc;

	
	@After
	public void tearDown() {
		context = null;

		mockMvc = null;
	}

	@Test
	public void createUser_DuplicateUsernameError() throws Exception {
		String accessToken = getAccessToken("cc_sysadmin", "password");
		String userDeatils = "{\"name\":\"Vivo SP Admin\",\"apiKey\":\"1edddb0c-06f6-41d4-9bad-2e2d38f26ae1\",\"username\":\"cc_sysadmin\",\"roles\":[\"USER\",\"ADMIN\"],\"baseUrl\":\"https://rws-jpotest.jasperwireless.com/rws\",\"billingCycleStartDay\":1,\"billingCyclePeriod\":31,\"deviceStates\":[\"ACTIVATED\",\"INVENTORY\",\"REPLACED\"],\"useAPIKey\":true,\"password\":\"password\"}";
		mockMvc.perform(post(BASE_URL + "/users").content(userDeatils)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)).andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.message", is("E11000 duplicate key error collection: cc_analytics.users index: username dup key: { : \"cc_sysadmin\" }")));
	}

	private String getAccessToken(String username, String password) throws Exception {
		String authorization = "Basic " + new String(Base64.encode("ashish:secret".getBytes()));
		String contentType = MediaType.APPLICATION_JSON + ";charset=UTF-8";

		// @formatter:off
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

		// @formatter:on

		return new JSONObject(content).getString("access_token");
	}

}
