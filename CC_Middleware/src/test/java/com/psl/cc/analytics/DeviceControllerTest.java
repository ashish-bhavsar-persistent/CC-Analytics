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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.util.encoders.Base64;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.psl.cc.analytics.controller.DeviceController;
import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.model.CCUser;
import com.psl.cc.analytics.model.Device;
import com.psl.cc.analytics.model.Role;
import com.psl.cc.analytics.response.AccountAggregation;
import com.psl.cc.analytics.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DeviceControllerTest {

	private final String accountInfo = "{\"accountName\":\"SIM Replacement Test\",\"accountId\":\"100007512\",\"type\":\"STANDARD\",\"status\":\"Active\",\"currency\":\"BRL\",\"operatorAccountId\":null,\"taxId\":\"-1\",\"commPlanDetails\":{\"defaultCommPlan\":\"Vivo Default CP\",\"defaultOnCommProfile\":\"VIVO_ON_CP\",\"defaultOffCommProfile\":\"Vivo_BRL_OFF_CP\",\"onCommProfileHlrTemplateId\":\"777777100003929\",\"offCommProfileHlrTemplateId\":\"777777100003930\"},\"defaultRatePlan\":{\"defaultRatePlanId\":20566,\"defaultRatePlanName\":\"Vivo Default RP\"}}";
	private final String deviceInfo1 = "{\"iccid\":\"89550679245000000538\",\"imsi\":\"724067968000365\",\"msisdn\":\"882357968000365\",\"imei\":\"\",\"status\":\"INVENTORY\",\"ratePlan\":\"Vivo Default RP\",\"communicationPlan\":\"Vivo Default CP\",\"customer\":null,\"endConsumerId\":null,\"dateActivated\":null,\"dateAdded\":\"2018-03-27 19:19:41.573+0000\",\"dateUpdated\":\"2019-01-16 11:17:47.622+0000\",\"dateShipped\":\"2019-01-15 23:00:00.000+0000\",\"accountId\":\"100007512\",\"fixedIPAddress\":null,\"operatorCustom1\":\"515045\",\"operatorCustom2\":\"\",\"operatorCustom3\":\"\",\"operatorCustom4\":\"\",\"operatorCustom5\":\"\",\"accountCustom1\":\"\",\"accountCustom2\":\"\",\"accountCustom3\":\"\",\"accountCustom4\":\"\",\"accountCustom5\":\"\",\"accountCustom6\":\"\",\"accountCustom7\":\"\",\"accountCustom8\":\"\",\"accountCustom9\":\"\",\"accountCustom10\":\"\",\"customerCustom1\":\"\",\"customerCustom2\":\"\",\"customerCustom3\":\"\",\"customerCustom4\":\"\",\"customerCustom5\":\"\",\"simNotes\":null,\"euiccid\":null,\"deviceID\":null,\"modemID\":null,\"globalSimType\":\"LOCAL_SUBSCRIPTION\"}";
	private final String deviceInfo2 = "{\"iccid\":\"89550679243000001184\",\"imsi\":\"724067968000041\",\"msisdn\":\"882357968000041\",\"imei\":\"\",\"status\":\"REPLACED\",\"ratePlan\":\"Vivo Default RP\",\"communicationPlan\":\"Vivo Default CP\",\"customer\":null,\"endConsumerId\":null,\"dateActivated\":\"2019-03-12 10:02:42.073+0000\",\"dateAdded\":\"2014-12-22 16:57:17.711+0000\",\"dateUpdated\":\"2019-05-28 16:47:47.411+0000\",\"dateShipped\":\"2019-03-11 23:00:00.000+0000\",\"accountId\":\"100007512\",\"fixedIPAddress\":null,\"operatorCustom1\":\"\",\"operatorCustom2\":\"\",\"operatorCustom3\":\"\",\"operatorCustom4\":\"\",\"operatorCustom5\":\"\",\"accountCustom1\":\"\",\"accountCustom2\":\"\",\"accountCustom3\":\"\",\"accountCustom4\":\"\",\"accountCustom5\":\"\",\"accountCustom6\":\"\",\"accountCustom7\":\"\",\"accountCustom8\":\"\",\"accountCustom9\":\"\",\"accountCustom10\":\"\",\"customerCustom1\":\"\",\"customerCustom2\":\"\",\"customerCustom3\":\"\",\"customerCustom4\":\"\",\"customerCustom5\":\"\",\"simNotes\":\"MSISDNTRANSFERTEST\",\"euiccid\":null,\"deviceID\":null,\"modemID\":null,\"globalSimType\":\"NONE\"}";
	private final String BASE_URL = "/api/v1";

//	@Autowired
//	WebApplicationContext context;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService mockUserService;
	
	@MockBean
	private DeviceController mockDeviceController;
	
//
//	ObjectMapper objectMapper = new ObjectMapper();

	@After
	public void tearDown() {
//		context = null;
		mockMvc = null;
//		mockUserService = null;
//		objectMapper = null;
	}

	@Before
	public void setUp() throws JsonParseException, JsonMappingException, IOException {
		List<Role> roles = new ArrayList<>();
		Role adminRole = new Role("ADMIN");
		Role userRole = new Role("USER");
		roles.add(adminRole);
		roles.add(userRole);
		CCUser vivoSpAdmin = new CCUser("5d17be9cbdf9360220be1cde", "VivoSpAdmin", "VivoSpAdmin",
				"$2a$10$X9CCAviNXslv7UO52Y/c8.58gqxkqnjee/aiAkoWdclNmfpn07Sfa", roles, true);

		when(mockUserService.findOneByUsername("VivoSpAdmin")).thenReturn(vivoSpAdmin);
//		Device device1 = objectMapper.readValue(deviceInfo1, Device.class);
//		Device device2 = objectMapper.readValue(deviceInfo2, Device.class);
//		AccountDTO accountDTO = objectMapper.readValue(accountInfo, AccountDTO.class);
//		List<Device> devices = new ArrayList<>();
//		devices.add(device1);
//		devices.add(device2);
//		accountDTO.setDeviceList(devices);
//		accountDTO.setUser(vivoSpAdmin);
//
		AccountAggregation aggregation = new AccountAggregation();
		aggregation.setRatePlan("Vivo Default RP");
		aggregation.setTotal(2l);
		List<AccountAggregation> aggregations = new ArrayList<>();

		aggregations.add(aggregation);
		when(mockDeviceController.getRatePlanCount("100007512")).thenReturn(aggregations);
//		when(mockUserService.findOneByUsername("VivoSpAdmin")).thenReturn(vivoSpAdmin);



	}

	@Test
	public void getRatePlanCount() throws Exception {
		String accessToken = getAccessToken("VivoSpAdmin", "password");
		mockMvc.perform(get(BASE_URL + "/devices/ratePlan").param("accountId", "100007512")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken)).andExpect(status().isOk());
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
