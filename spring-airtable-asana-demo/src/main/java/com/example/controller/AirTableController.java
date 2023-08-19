package com.example.controller;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestTemplate;

public class AirTableController {
    private static final String AIRTABLE_API_KEY = "pat8zChaydMFbf8US.ab36039b01ce2ea500772963e8f4076146539ba03760fe143d183660c56c4ac4";
    private static final String AIRTABLE_BASE_ID = "appDaSRBhh7wYSu1C";
    private static final String AIRTABLE_TABLE_NAME = "sampletable";
    private static final String AIRTABLE_API_URL = "https://api.airtable.com/v0/" + AIRTABLE_BASE_ID + "/" + AIRTABLE_TABLE_NAME;

    
	 private static final String SECRET = "your_webhook_secret";
	    private static final String ASANA_EVENT_HEADER = "X-Hook-Secret";
	    private static final String ASANA_EVENT_URL = "https://your_asana_webhook_url";

	    @PostMapping("/webhook")
	    public void handleWebhook(
	            @RequestHeader String userAgent,
	            @RequestHeader(ASANA_EVENT_HEADER) String asanaHookSecret,
	            @RequestBody String requestBody) {

	        if (SECRET.equals(asanaHookSecret)) {

	        	getdatafromasana();

	            sendAcknowledgment();

	        } else {
	            System.out.println("Unauthorized request.");

	        }
	    }

	    private void sendAcknowledgment() {
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);

	        RestTemplate restTemplate = new RestTemplate();
	        restTemplate.postForObject(ASANA_EVENT_URL, null, String.class, headers);
	    }
	    
	    private static void createTaskInAirtable(String name, String assignee, String dueDate, String description,String taskId) {
	        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
	            HttpPost httpPost = new HttpPost(AIRTABLE_API_URL);

	            // Set Airtable API headers
	            httpPost.setHeader("Authorization", "Bearer " + AIRTABLE_API_KEY);
	            httpPost.setHeader("Content-Type", "application/json");

	            // Create JSON payload
	            String jsonPayload = String.format(
	                "{\"fields\": {\"Name\": \"%s\", \"Assignee\": \"%s\", \"Due Date\": \"%s\", \"Description\": \"%s\",\"TaskID\": \"%s\"}}",
	                 name, assignee, dueDate, description,taskId);
	            httpPost.setEntity(new StringEntity(jsonPayload));
	            // Execute the request
	            httpClient.execute(httpPost);
	            System.out.println("Task created in Airtable.");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    
	    private static void getdatafromasana(){
	    	try {
	    		 String assignee = "Mohith";
	    	        String dueDate = "Monday";
	    	        String description = "This is a sample task created in Asana.";
	    	    	
	    	    	
	    	    	String accessToken = "1/1205300646210396:70905f400bb3414a191ac3f9e6ef9faf";
	    	        String projectId = "1205300648061331";

	    	        String apiUrl = "https://app.asana.com/api/1.0/projects/" + projectId + "/tasks";
	    	        URL url = new URL(apiUrl);

	    	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	    	        connection.setRequestMethod("GET");
	    	        connection.setRequestProperty("Authorization", "Bearer " + accessToken);

	    	        int responseCode = connection.getResponseCode();

	    	        if (responseCode == HttpURLConnection.HTTP_OK) {
	    	            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    	            String inputLine;
	    	            StringBuilder response = new StringBuilder();

	    	            while ((inputLine = in.readLine()) != null) {
	    	                response.append(inputLine);
	    	            }
	    	            in.close();

	    	            System.out.println(response.toString()); // This will print the fetched data
	    	            JSONObject AsanaResp=new JSONObject(response.toString());
	    	            JSONArray arr=AsanaResp.getJSONArray("data");
	    	            for(int i=0;i<arr.length();i++){
	    	            	JSONObject json=arr.getJSONObject(i);
	    	            	String taskId=json.getString("gid");
	    	            	String taskName=json.getString("name");
	    	                // Create a new task in Airtable
	    	                createTaskInAirtable(taskName, assignee, dueDate, description,taskId);
	    	            }
	    	        } else {
	    	            System.out.println("Request failed with response code: " + responseCode);
	    	        }
	    	    	
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    }

}
