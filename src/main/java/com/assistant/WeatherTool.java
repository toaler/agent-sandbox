package com.assistant;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class WeatherTool {
    private final String apiKey;
    private final HttpClient httpClient;

    public WeatherTool(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Tool("Returns the current weather for a given location")
    public String currentWeather(
            @P("The city, address or lat/long to get weather for") String location) {
        try {
            String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric",
                location.replace(" ", "%20"), apiKey
            );
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                return String.format("Error retrieving weather for %s: HTTP %d", location, response.statusCode());
            }
            
            JSONObject json = new JSONObject(response.body());
            JSONObject main = json.getJSONObject("main");
            JSONObject wind = json.getJSONObject("wind");
            String description = json.getJSONArray("weather").getJSONObject(0).getString("description");
            
            return String.format("Current weather in %s:\n" +
                    "Temperature: %.1f°C\n" +
                    "Description: %s\n" +
                    "Humidity: %d%%\n" +
                    "Wind Speed: %.1f m/s",
                    location,
                    main.getDouble("temp"),
                    description,
                    main.getInt("humidity"),
                    wind.getDouble("speed"));
        } catch (IOException | InterruptedException e) {
            return String.format("Error retrieving weather for %s: %s", location, e.getMessage());
        }
    }
} 