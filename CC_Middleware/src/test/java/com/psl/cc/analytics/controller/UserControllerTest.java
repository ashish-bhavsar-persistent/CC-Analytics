package com.psl.cc.analytics.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.bouncycastle.util.encoders.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.psl.cc.analytics.util.UserControllerUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserControllerTest {

	private final String BASE_URL = "/api/v1";

	@Autowired
	private UserControllerUtil utils;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void getLoginPage() throws Exception {
		mockMvc.perform(get(BASE_URL + "/index.html").accept(MediaType.TEXT_HTML)).andExpect(status().isOk());
	}
	
	@Test
	public void getLoginPage_OPtions() throws Exception {
		mockMvc.perform(options(BASE_URL + "/index.html").accept(MediaType.TEXT_HTML)).andExpect(status().isOk());
	}

	@Test
	public void getAllUsers_withoutToken() throws Exception {
		mockMvc.perform(get(BASE_URL + "/users").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized()).andExpect(jsonPath("$.error", is("unauthorized")));
	}

	@Test
	public void getJwtToken_UserNotFound() throws Exception {
		String username = "cc_sysadmin1";
		String password = "password1";
		String authorization = "Basic " + new String(Base64.encode("ashish:secret".getBytes()));

		mockMvc.perform(post("/oauth/token").header("Authorization", authorization)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).param("username", username)
				.param("password", password).param("grant_type", "password").param("scope", "read write"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.error_description", is("Bad credentials")));
	}

	@Test
	public void getAllUsers() throws Exception {
		String accessToken = getAccessToken("cc_sysadmin", "password");

		String users = mockMvc.perform(get(BASE_URL + "/users").header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].id", is(notNullValue())))
				.andExpect(jsonPath("$[0].username", is("cc_sysadmin")))
				.andExpect(jsonPath("$[1].username", is("VivoSpAdmin")))
				.andExpect(jsonPath("$[1].apiKey", is("1edddb0c-06f6-41d4-9bad-2e2d38f26ae1")))
				.andExpect(jsonPath("$[1].baseUrl", is("https://rws-jpotest.jasperwireless.com/rws"))).andReturn()
				.getResponse().getContentAsString();
		JSONArray array = new JSONArray(users);
		array.forEach(data -> {
			JSONObject obj = (JSONObject) data;
			if ("VivoSpAdmin".equals(obj.getString("username")))
				utils.getData().put("VivoSpAdmin", obj.getString("id"));
		});

	}

	@Test
	public void getCurrentUser() throws Exception {
		String accessToken = getAccessToken("VivoSpAdmin", "password");
		mockMvc.perform(get(BASE_URL + "/users/me").header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id", is(notNullValue())))
				.andExpect(jsonPath("$.username", is("VivoSpAdmin")))
				.andExpect(jsonPath("$.apiKey", is("1edddb0c-06f6-41d4-9bad-2e2d38f26ae1")))
				.andExpect(jsonPath("$.baseUrl", is("https://rws-jpotest.jasperwireless.com/rws")));

	}

	@Test
	public void createUser() throws Exception {
		utils.setup();
		String accessToken = getAccessToken("cc_sysadmin", "password");
		String userDeatils = "{\"name\":\"Vivo SP Admin\",\"apiKey\":\"1edddb0c-06f6-41d4-9bad-2e2d38f26ae1\",\"username\":\"VivoSpAdmin\",\"roles\":[\"USER\",\"ADMIN\"],\"baseUrl\":\"https://rws-jpotest.jasperwireless.com/rws\",\"billingCycleStartDay\":1,\"billingCyclePeriod\":31,\"deviceStates\":[\"ACTIVATED\",\"INVENTORY\",\"REPLACED\"],\"useAPIKey\":true,\"password\":\"password\"}";
		mockMvc.perform(post(BASE_URL + "/users").content(userDeatils)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)).andExpect(status().isCreated());
	}

	@Test
	public void createUser_MissingField() throws Exception {
		String accessToken = getAccessToken("cc_sysadmin", "password");
		String userDeatils = "{\"apiKey\":\"1edddb0c-06f6-41d4-9bad-2e2d38f26ae1\",\"username\":\"VivoSpAdmin\",\"roles\":[\"USER\",\"ADMIN\"],\"baseUrl\":\"https://rws-jpotest.jasperwireless.com/rws\",\"billingCycleStartDay\":1,\"billingCyclePeriod\":31,\"deviceStates\":[\"ACTIVATED\",\"INVENTORY\",\"REPLACED\"],\"useAPIKey\":true,\"password\":\"password\"}";
		mockMvc.perform(post(BASE_URL + "/users").content(userDeatils)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Please provide Name")));
	}

	@Test
	public void createUser_ValidationError_AllTrue() throws Exception {
		String accessToken = getAccessToken("cc_sysadmin", "password");
		String userDeatils = "{\"name\":\"Vivo SP Admin\",\"apiKey\":\"1edddb0c-06f6-41d4-9bad-2e2d38f26ae1\",\"username\":\"VivoSpAdmin\",\"roles\":[\"USER\",\"ADMIN\"],\"baseUrl\":\"https://rws-jpotest.jasperwireless.com/rws\",\"billingCycleStartDay\":1,\"billingCyclePeriod\":31,\"deviceStates\":[\"ACTIVATED\",\"INVENTORY\",\"REPLACED\"],\"useAPIKey\":true,\"usePassword\":true,\"password\":\"password\"}";
		mockMvc.perform(post(BASE_URL + "/users").content(userDeatils)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Both useAPIKey and usePassword can not be true")));

	}

	@Test
	public void createUser_ValidationError_AllFalse() throws Exception {
		String accessToken = getAccessToken("cc_sysadmin", "password");
		String userDeatils = "{\"name\":\"Vivo SP Admin\",\"apiKey\":\"1edddb0c-06f6-41d4-9bad-2e2d38f26ae1\",\"username\":\"VivoSpAdmin\",\"roles\":[\"USER\",\"ADMIN\"],\"baseUrl\":\"https://rws-jpotest.jasperwireless.com/rws\",\"billingCycleStartDay\":1,\"billingCyclePeriod\":31,\"deviceStates\":[\"ACTIVATED\",\"INVENTORY\",\"REPLACED\"],\"useAPIKey\":false,\"usePassword\":false,\"password\":\"password\"}";
		mockMvc.perform(post(BASE_URL + "/users").content(userDeatils)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Both useAPIKey and usePassword can not be false")));
	}

	@Test
	public void createUser_RoleNotFound() throws Exception {
		String accessToken = getAccessToken("cc_sysadmin", "password");
		String userDeatils = "{\"name\":\"Vivo SP Admin\",\"apiKey\":\"1edddb0c-06f6-41d4-9bad-2e2d38f26ae1\",\"username\":\"VivoSpAdmin\",\"roles\":[\"USER\",\"ADMIN\",\"MYROLE\"],\"baseUrl\":\"https://rws-jpotest.jasperwireless.com/rws\",\"billingCycleStartDay\":1,\"billingCyclePeriod\":31,\"deviceStates\":[\"ACTIVATED\",\"INVENTORY\",\"REPLACED\"],\"useAPIKey\":true,\"password\":\"password\"}";
		mockMvc.perform(post(BASE_URL + "/users").content(userDeatils)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message", is("MYROLE: Not Found")));
	}

	@Test
	public void updateUser() throws Exception {
		String accessToken = getAccessToken("cc_sysadmin", "password");
		String userDeatils = "{\"name\":\"Vivo SP Admin\",\"apiKey\":\"1edddb0c-06f6-41d4-9bad-2e2d38f26ae1\",\"username\":\"VivoSpAdmin\",\"roles\":[\"USER\",\"ADMIN\"],\"baseUrl\":\"https://rws-jpotest.jasperwireless.com/rws\",\"billingCycleStartDay\":1,\"billingCyclePeriod\":31,\"deviceStates\":[\"ACTIVATED\",\"INVENTORY\",\"REPLACED\"],\"useAPIKey\":true,\"password\":\"password\"}";
		mockMvc.perform(put(BASE_URL + "/users/" + utils.getData().getString("VivoSpAdmin")).content(userDeatils)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)).andExpect(status().isCreated());
	}

	@Test
	public void updateUser_ValidationError_AllTrue() throws Exception {
		String accessToken = getAccessToken("cc_sysadmin", "password");
		String userDeatils = "{\"name\":\"Vivo SP Admin\",\"apiKey\":\"1edddb0c-06f6-41d4-9bad-2e2d38f26ae1\",\"username\":\"VivoSpAdmin\",\"roles\":[\"USER\",\"ADMIN\"],\"baseUrl\":\"https://rws-jpotest.jasperwireless.com/rws\",\"billingCycleStartDay\":1,\"billingCyclePeriod\":31,\"deviceStates\":[\"ACTIVATED\",\"INVENTORY\",\"REPLACED\"],\"useAPIKey\":true,\"usePassword\":true,\"password\":\"password\"}";
		mockMvc.perform(put(BASE_URL + "/users/" + utils.getData().getString("VivoSpAdmin")).content(userDeatils)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Both useAPIKey and usePassword can not be true")));
		utils.tearDown();
	}

	@Test
	public void updateUser_ValidationError_AllFalse() throws Exception {
		String accessToken = getAccessToken("cc_sysadmin", "password");
		String userDeatils = "{\"name\":\"Vivo SP Admin\",\"apiKey\":\"1edddb0c-06f6-41d4-9bad-2e2d38f26ae1\",\"username\":\"VivoSpAdmin\",\"roles\":[\"USER\",\"ADMIN\"],\"baseUrl\":\"https://rws-jpotest.jasperwireless.com/rws\",\"billingCycleStartDay\":1,\"billingCyclePeriod\":31,\"deviceStates\":[\"ACTIVATED\",\"INVENTORY\",\"REPLACED\"],\"useAPIKey\":false,\"usePassword\":false,\"password\":\"password\"}";
		mockMvc.perform(put(BASE_URL + "/users/2").content(userDeatils)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Both useAPIKey and usePassword can not be false")));
	}

	@Test
	public void updateUser_RoleNotFound() throws Exception {
		String accessToken = getAccessToken("cc_sysadmin", "password");
		String userDeatils = "{\"name\":\"Vivo SP Admin\",\"apiKey\":\"1edddb0c-06f6-41d4-9bad-2e2d38f26ae1\",\"username\":\"VivoSpAdmin\",\"roles\":[\"USER\",\"ADMIN\",\"MYROLE\"],\"baseUrl\":\"https://rws-jpotest.jasperwireless.com/rws\",\"billingCycleStartDay\":1,\"billingCyclePeriod\":31,\"deviceStates\":[\"ACTIVATED\",\"INVENTORY\",\"REPLACED\"],\"useAPIKey\":true,\"password\":\"password\"}";
		mockMvc.perform(put(BASE_URL + "/users/" + utils.getData().getString("VivoSpAdmin")).content(userDeatils)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message", is("MYROLE: Not Found")));
	}

	@Test
	public void updateUser_NotFound() throws Exception {
		String accessToken = getAccessToken("cc_sysadmin", "password");
		String userDeatils = "{\"name\":\"Vivo SP Admin\",\"apiKey\":\"1edddb0c-06f6-41d4-9bad-2e2d38f26ae1\",\"username\":\"VivoSpAdmin\",\"roles\":[\"USER\",\"ADMIN\",\"MYROLE\"],\"baseUrl\":\"https://rws-jpotest.jasperwireless.com/rws\",\"billingCycleStartDay\":1,\"billingCyclePeriod\":31,\"deviceStates\":[\"ACTIVATED\",\"INVENTORY\",\"REPLACED\"],\"useAPIKey\":true,\"password\":\"password\"}";
		mockMvc.perform(put(BASE_URL + "/users/3").content(userDeatils)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message", is("3: Not Found")));
	}

	@Test
	public void deleteUserById() throws Exception {
		String accessToken = getAccessToken("cc_sysadmin", "password");
		mockMvc.perform(delete(BASE_URL + "/users/" + utils.getData().getString("deleteUser")).header("Authorization",
				"Bearer " + accessToken)).andExpect(status().isOk());
	}

	@Test
	public void deleteUserByIdWithConfig() throws Exception {
		String accessToken = getAccessToken("cc_sysadmin", "password");
		mockMvc.perform(delete(BASE_URL + "/users/" + utils.getData().getString("deleteUser2")).header("Authorization",
				"Bearer " + accessToken)).andExpect(status().isOk());
	}

	@Test
	public void deleteUserById_NotFound() throws Exception {
		String accessToken = getAccessToken("cc_sysadmin", "password");
		mockMvc.perform(delete(BASE_URL + "/users/3").header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.message", is("3: Not Found")));
	}

	@Test
	public void getUserById() throws Exception {
		String accessToken = getAccessToken("cc_sysadmin", "password");
		mockMvc.perform(get(BASE_URL + "/users/" + utils.getData().getString("VivoSpAdmin"))
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(notNullValue()))).andExpect(jsonPath("$.username", is("VivoSpAdmin")))
				.andExpect(jsonPath("$.apiKey", is("1edddb0c-06f6-41d4-9bad-2e2d38f26ae1")))
				.andExpect(jsonPath("$.baseUrl", is("https://rws-jpotest.jasperwireless.com/rws")));
	}

	@Test
	public void getUserById_NotFound() throws Exception {
		String accessToken = getAccessToken("cc_sysadmin", "password");
		mockMvc.perform(get(BASE_URL + "/users/2").header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message", is("2: Not Found")));
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
