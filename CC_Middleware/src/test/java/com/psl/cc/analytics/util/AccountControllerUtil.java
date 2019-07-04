package com.psl.cc.analytics.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.psl.cc.analytics.model.AccountDTO;
import com.psl.cc.analytics.model.CCUser;
import com.psl.cc.analytics.model.Configuration;
import com.psl.cc.analytics.model.Device;
import com.psl.cc.analytics.model.Role;
import com.psl.cc.analytics.repository.ConfigurationRepository;
import com.psl.cc.analytics.repository.RoleRepository;
import com.psl.cc.analytics.service.AccountService;
import com.psl.cc.analytics.service.DeviceService;
import com.psl.cc.analytics.service.UserService;

@Service
public class AccountControllerUtil {

	private final String accountInfo = "{\"accountName\":\"SIM Replacement Test\",\"accountId\":\"100007512\",\"type\":\"STANDARD\",\"status\":\"Active\",\"currency\":\"BRL\",\"operatorAccountId\":null,\"taxId\":\"-1\",\"commPlanDetails\":{\"defaultCommPlan\":\"Vivo Default CP\",\"defaultOnCommProfile\":\"VIVO_ON_CP\",\"defaultOffCommProfile\":\"Vivo_BRL_OFF_CP\",\"onCommProfileHlrTemplateId\":\"777777100003929\",\"offCommProfileHlrTemplateId\":\"777777100003930\"},\"defaultRatePlan\":{\"defaultRatePlanId\":20566,\"defaultRatePlanName\":\"Vivo Default RP\"}}";
	private final String deviceInfo1 = "{\"iccid\":\"89550679243000001184\",\"imsi\":\"724067968000041\",\"msisdn\":\"882357968000041\",\"imei\":\"\",\"status\":\"REPLACED\",\"ratePlan\":\"Vivo Default RP\",\"communicationPlan\":\"Vivo Default CP\",\"customer\":null,\"endConsumerId\":null,\"dateActivated\":\"2019-03-12 10:02:42.073+0000\",\"dateAdded\":\"2014-12-22 16:57:17.711+0000\",\"dateUpdated\":\"2019-05-28 16:47:47.411+0000\",\"dateShipped\":\"2019-03-11 23:00:00.000+0000\",\"accountId\":\"100007512\",\"fixedIPAddress\":null,\"operatorCustom1\":\"\",\"operatorCustom2\":\"\",\"operatorCustom3\":\"\",\"operatorCustom4\":\"\",\"operatorCustom5\":\"\",\"accountCustom1\":\"\",\"accountCustom2\":\"\",\"accountCustom3\":\"\",\"accountCustom4\":\"\",\"accountCustom5\":\"\",\"accountCustom6\":\"\",\"accountCustom7\":\"\",\"accountCustom8\":\"\",\"accountCustom9\":\"\",\"accountCustom10\":\"\",\"customerCustom1\":\"\",\"customerCustom2\":\"\",\"customerCustom3\":\"\",\"customerCustom4\":\"\",\"customerCustom5\":\"\",\"simNotes\":\"MSISDNTRANSFERTEST\",\"euiccid\":null,\"deviceID\":null,\"modemID\":null,\"globalSimType\":\"NONE\"}";
	private final String deviceInfo2 = "{\"iccid\":\"89550679245000000538\",\"imsi\":\"724067968000365\",\"msisdn\":\"882357968000365\",\"imei\":\"\",\"status\":\"INVENTORY\",\"ratePlan\":\"Vivo Default RP\",\"communicationPlan\":\"Vivo Default CP\",\"customer\":null,\"endConsumerId\":null,\"dateActivated\":null,\"dateAdded\":\"2018-03-27 19:19:41.573+0000\",\"dateUpdated\":\"2019-01-16 11:17:47.622+0000\",\"dateShipped\":\"2019-01-15 23:00:00.000+0000\",\"accountId\":\"100007512\",\"fixedIPAddress\":null,\"operatorCustom1\":\"515045\",\"operatorCustom2\":\"\",\"operatorCustom3\":\"\",\"operatorCustom4\":\"\",\"operatorCustom5\":\"\",\"accountCustom1\":\"\",\"accountCustom2\":\"\",\"accountCustom3\":\"\",\"accountCustom4\":\"\",\"accountCustom5\":\"\",\"accountCustom6\":\"\",\"accountCustom7\":\"\",\"accountCustom8\":\"\",\"accountCustom9\":\"\",\"accountCustom10\":\"\",\"customerCustom1\":\"\",\"customerCustom2\":\"\",\"customerCustom3\":\"\",\"customerCustom4\":\"\",\"customerCustom5\":\"\",\"simNotes\":null,\"euiccid\":null,\"deviceID\":null,\"modemID\":null,\"globalSimType\":\"LOCAL_SUBSCRIPTION\"}";

	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private AccountService accountService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserService userService;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private ConfigurationRepository configurationRepository;

	@Autowired
	org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

	JSONObject data = new JSONObject();

	public void setup() throws JsonParseException, JsonMappingException, IOException {
		tearDown();
		Role adminRole = new Role("ADMIN");
		roleRepository.save(adminRole);

		Role sysRole = new Role("SYSADMIN");
		roleRepository.save(sysRole);

		Role userRole = new Role("USER");
		roleRepository.save(userRole);

		List<Role> rolesTest = new ArrayList<>();
		rolesTest.add(adminRole);
		CCUser test = new CCUser("Vivo Test", "Test", passwordEncoder.encode("password"), rolesTest, true);
		userService.save(test);

		rolesTest.add(sysRole);
		CCUser test1 = new CCUser("Vivo Test", "Test1", passwordEncoder.encode("password"), rolesTest, true);
		userService.save(test1);

		rolesTest.clear();
		rolesTest.add(userRole);
		CCUser user1 = new CCUser("Vivo Test", "100007512", passwordEncoder.encode("password"), rolesTest, true);
		userService.save(user1);

		List<Role> roles = new ArrayList<>();
		roles.add(adminRole);
		CCUser user = new CCUser("Vivo Sp Admin", "VivoSpAdmin", passwordEncoder.encode("password"), roles, true);
		userService.save(user);

		List<String> status = new ArrayList<>();
		status.add("Activated");
		Configuration config = new Configuration(user, "https://rws-jpotest.jasperwireless.com/rws", 1, 31, status,
				false, true, "1edddb0c-06f6-41d4-9bad-2e2d38f26ae1");
		configurationRepository.save(config);

		AccountDTO account = objectMapper.readValue(accountInfo, AccountDTO.class);
		Device device1 = objectMapper.readValue(deviceInfo1, Device.class);
		Device device2 = objectMapper.readValue(deviceInfo2, Device.class);

		deviceService.save(device1);
		deviceService.save(device2);

		account.setUser(user);
		account.setCreatedOn(new Date());
		account.setLastUpdatedOn(new Date());
		accountService.save(account);

		data.put("adminId", user.getId());
	}

	public JSONObject getData() {
		return data;
	}

	public void tearDown() {
		mongoTemplate.getDb().drop();
	}
}
