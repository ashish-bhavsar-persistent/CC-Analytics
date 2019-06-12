package sample;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class GetDeviceDetails implements Callable<JSONObject> {

	private String accountId;
	private String deviceId;

	public GetDeviceDetails(String accountId, String deviceId) {
		this.accountId = accountId;
		this.deviceId = deviceId;
	}

	@Override
	public JSONObject call() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth("VivoSpAdmin", "1edddb0c-06f6-41d4-9bad-2e2d38f26ae1");
		HttpEntity<String> request = new HttpEntity<String>(headers);
		RestTemplate restTemplate = new RestTemplate();
		String url = ("https://rws-jpotest.jasperwireless.com/rws" + "/api/v1/devices/");
		URI uri = null;
		uri = UriComponentsBuilder.fromUriString(url).path(deviceId).build(true).toUri();

		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			JSONObject deviceObject = new JSONObject(response.getBody().toString());
			deviceObject.put("sp_id", accountId);
			return deviceObject;
		}
		return null;
	}

}
