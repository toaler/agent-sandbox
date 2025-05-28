package com.assistant;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;
import org.json.JSONArray;

public class DirectionsTool {
    private final String apiKey;
    private final HttpClient httpClient;

    public DirectionsTool(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Tool("Returns driving directions between two locations")
    public String getDirections(
            @P("The starting address or location") String origin,
            @P("The destination address or location") String destination) {
        try {
            String url = String.format(
                "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=%s",
                origin.replace(" ", "+"), destination.replace(" ", "+"), apiKey
            );
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                return String.format("Error retrieving directions: HTTP %d", response.statusCode());
            }
            
            JSONObject json = new JSONObject(response.body());
            if (!json.getString("status").equals("OK")) {
                return String.format("Error retrieving directions: %s", json.getString("status"));
            }
            
            JSONObject route = json.getJSONArray("routes").getJSONObject(0);
            JSONObject leg = route.getJSONArray("legs").getJSONObject(0);
            
            StringBuilder directions = new StringBuilder();
            directions.append(String.format("Directions from %s to %s:\n", origin, destination));
            directions.append(String.format("Total distance: %s\n", leg.getJSONObject("distance").getString("text")));
            directions.append(String.format("Estimated duration: %s\n\n", leg.getJSONObject("duration").getString("text")));
            
            JSONArray steps = leg.getJSONArray("steps");
            for (int i = 0; i < steps.length(); i++) {
                JSONObject step = steps.getJSONObject(i);
                directions.append(String.format("%d. %s\n", i + 1, step.getString("html_instructions").replaceAll("<[^>]*>", "")));
            }
            
            return directions.toString();
        } catch (IOException | InterruptedException e) {
            return String.format("Error retrieving directions: %s", e.getMessage());
        }
    }
} 