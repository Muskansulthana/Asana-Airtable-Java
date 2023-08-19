package com.example.demo;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;


public class AirTable {

    private static final String AIRTABLE_API_KEY = "pat8zChaydMFbf8US.ab36039b01ce2ea500772963e8f4076146539ba03760fe143d183660c56c4ac4";
    private static final String AIRTABLE_BASE_ID = "appDaSRBhh7wYSu1C";
    private static final String AIRTABLE_TABLE_NAME = "sampletable";
    private static final String AIRTABLE_API_URL = "https://api.airtable.com/v0/" + AIRTABLE_BASE_ID + "/" + AIRTABLE_TABLE_NAME;

    public static void main(String[] args) throws IOException{
        String assignee = "Muskan";
        String dueDate = "Friday";
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
    	
        // Create a new task in Airtable
//        createTaskInAirtable(taskName, assignee, dueDate, description);
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
}
