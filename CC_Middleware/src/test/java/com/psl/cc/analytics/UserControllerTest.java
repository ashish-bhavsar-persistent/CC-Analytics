package com.psl.cc.analytics;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.util.encoders.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.psl.cc.analytics.model.CCUser;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.model.Role;
import com.psl.cc.analytics.repository.ConfigurationRepository;
import com.psl.cc.analytics.response.User;
import com.psl.cc.analytics.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

	private static final String BASE_URL = "/api/v1";
	private static final ObjectMapper OM = new ObjectMapper();

	@Autowired
	WebApplicationContext context;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService mockUserService;

	@MockBean
	private ConfigurationRepository configurationRepository;

	@Before
	public void setUp() {

		List<Role> sysRoles = new ArrayList<>();
		Role sysAdminRole = new Role("SYSADMIN");
		sysRoles.add(sysAdminRole);
		CCUser cc_sysadmin = new CCUser("1", "cc_sysadmin", "cc_sysadmin",
				"$2a$10$X9CCAviNXslv7UO52Y/c8.58gqxkqnjee/aiAkoWdclNmfpn07Sfa", sysRoles, true);
		List<Role> roles = new ArrayList<>();
		Role adminRole = new Role("ADMIN");
		Role userRole = new Role("USER");
		roles.add(adminRole);
		roles.add(userRole);
		CCUser vivoSpAdmin = new CCUser("2", "VivoSpAdmin", "VivoSpAdmin",
				"$2a$10$X9CCAviNXslv7UO52Y/c8.58gqxkqnjee/aiAkoWdclNmfpn07Sfa", roles, true);

		List<CCUser> users = new ArrayList<>();
		users.add(cc_sysadmin);
		users.add(vivoSpAdmin);
		when(mockUserService.findOneByUsername("cc_sysadmin")).thenReturn(cc_sysadmin);
		when(mockUserService.findOneByUsername("VivoSpAdmin")).thenReturn(vivoSpAdmin);
		when(mockUserService.findAll()).thenReturn(users);
		List<String> deviceStates = new ArrayList<>();
		deviceStates.add("ACTIVATED");
		deviceStates.add("INVENTORY");
		deviceStates.add("REPLACED");
		deviceStates.add("TEST_READY");
		Configuration configuration = new Configuration("1", vivoSpAdmin, "https://rws-jpotest.jasperwireless.com/rws",
				1, 31, deviceStates, false, true, "1edddb0c-06f6-41d4-9bad-2e2d38f26ae1");
		when(configurationRepository.findOneByUserId("2")).thenReturn(configuration);
	}

	@Test
	public void getAllUsers_withoutToken() throws Exception {
		mockMvc.perform(get(BASE_URL + "/users").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized()).andExpect(jsonPath("$.error", is("unauthorized")));
	}

	@Test
	public void getAllUsers() throws Exception {
		String accessToken = getAccessToken("cc_sysadmin", "password");

		mockMvc.perform(get(BASE_URL + "/users").header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].id", is(notNullValue())))
				.andExpect(jsonPath("$[0].username", is("cc_sysadmin")))
				.andExpect(jsonPath("$[1].username", is("VivoSpAdmin")))
				.andExpect(jsonPath("$[1].apiKey", is("1edddb0c-06f6-41d4-9bad-2e2d38f26ae1")))
				.andExpect(jsonPath("$[1].baseUrl", is("https://rws-jpotest.jasperwireless.com/rws")));

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
		String accessToken = getAccessToken("cc_sysadmin", "password");
		String userDeatils = "{\"name\":\"Vivo SP Admin\",\"apiKey\":\"1edddb0c-06f6-41d4-9bad-2e2d38f26ae1\",\"username\":\"VivoSpAdmin\",\"roles\":[\"USER\",\"ADMIN\"],\"baseUrl\":\"https://rws-jpotest.jasperwireless.com/rws\",\"billingCycleStartDay\":1,\"billingCyclePeriod\":31,\"deviceStates\":[\"ACTIVATED\",\"INVENTORY\",\"REPLACED\"],\"useAPIKey\":true,\"password\":\"password\"}";
		mockMvc.perform(post(BASE_URL + "/users").content(userDeatils).header(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isInternalServerError());
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
