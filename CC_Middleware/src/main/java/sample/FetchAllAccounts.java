package sample;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class FetchAllAccounts implements Callable<String> {

	public String call() throws Exception {
		Map<String, JSONObject> accountsMap = new HashMap<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth("VivoSpAdmin", "1edddb0c-06f6-41d4-9bad-2e2d38f26ae1");
		HttpEntity<String> request = new HttpEntity<String>(headers);
		RestTemplate restTemplate = new RestTemplate();
		String url = ("https://rws-jpotest.jasperwireless.com/rws" + "/api/v1/accounts");
		boolean lastPage = false;
		int pageNumber = 1;
		do {
//			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParam("pageNumber", pageNumber);
			Map<String, String> params = new HashMap<String, String>();
			params.put("pageNumber", String.valueOf(pageNumber));
			URI uri = null;
			uri = UriComponentsBuilder.fromUriString(url).queryParam("pageNumber", String.valueOf(pageNumber)).build()
					.toUri();
			ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
			if (response.getStatusCode() == HttpStatus.OK) {
				JSONObject accountsObject = new JSONObject(response.getBody().toString());
				System.out.println(accountsObject);
				lastPage = accountsObject.getBoolean("lastPage");
				JSONArray accounts = accountsObject.getJSONArray("accounts");
				for (Object Obj : accounts) {
					JSONObject account = new JSONObject(Obj.toString());
					String accountId = account.getString("accountId");
					accountsMap.put(accountId, account);
				}
				pageNumber++;
			}
		} while (!lastPage);
		System.out.println(accountsMap.size());
		ExecutorService executor = Executors.newFixedThreadPool(5);
		List<Future<JSONObject>> futureList = new ArrayList<>();
		List<Future<JSONObject>> futureListOfDevices = new ArrayList<>();

		for (String accountId : accountsMap.keySet()) {
			Future<JSONObject> future = executor.submit(new FetchDevicesOfAccount(accountId));
			futureList.add(future);
		}
		int i = 0;
		try {

			for (Future<JSONObject> f : futureList) {
				JSONObject obj = f.get();
				JSONArray deviceArray = obj.getJSONArray("devices");
				for (Object device : deviceArray) {
					JSONObject deviceObject = new JSONObject(device.toString());
					Future<JSONObject> future = executor
							.submit(new GetDeviceDetails(obj.getString("accountId"), deviceObject.getString("iccid")));
					futureListOfDevices.add(future);
				}

			}
			for (Future<JSONObject> f : futureListOfDevices) {
				JSONObject obj = f.get();
				System.out.println(obj);
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Total Devices " + i);
		return null;
	}

}
