package sample;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class FetchDevicesOfAccount implements Callable<JSONObject> {

	private String accountId;

	public FetchDevicesOfAccount(String accountId) {
		this.accountId = accountId;
	}

	@Override
	public JSONObject call() throws Exception {
		JSONObject deviceJson = new JSONObject();
		JSONArray array = null;

		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth("VivoSpAdmin", "1edddb0c-06f6-41d4-9bad-2e2d38f26ae1");
		HttpEntity<String> request = new HttpEntity<String>(headers);
		RestTemplate restTemplate = new RestTemplate();
		String url = ("https://rws-jpotest.jasperwireless.com/rws" + "/api/v1/devices");
		boolean lastPage = false;
		int pageNumber = 1;
		do {

			URI uri = null;
			uri = UriComponentsBuilder.fromUriString(url).queryParam("pageNumber", String.valueOf(pageNumber))
					.queryParam("modifiedSince",
							URLEncoder.encode("2000-06-12T13:44:28+05:30", StandardCharsets.UTF_8.toString()))
					.queryParam("accountId", accountId).build(true).toUri();

			ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
			if (response.getStatusCode() == HttpStatus.OK) {
				JSONObject deviceObject = new JSONObject(response.getBody().toString());
				lastPage = deviceObject.getBoolean("lastPage");
				JSONArray devices = deviceObject.getJSONArray("devices");
				if (array == null) {
					array = new JSONArray(devices.toString());
				} else
					for (Object obj : devices) {
						array.put(new JSONObject(obj.toString()));
					}
				pageNumber++;
			}

		} while (!lastPage);
//		System.out.println(array);
		deviceJson.put("accountId", accountId);
		deviceJson.put("devices", array);
		return deviceJson;
	}
}
