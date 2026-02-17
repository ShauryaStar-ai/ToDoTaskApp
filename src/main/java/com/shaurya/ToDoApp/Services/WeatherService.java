package com.shaurya.ToDoApp.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    @Value("${weatherStack.apiKey}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public String getCurrentWeather(String city) {
        String url = "http://api.weatherstack.com/current?access_key=" + apiKey + "&query=" + city;
        String response = restTemplate.getForObject(url, String.class);
        
        if (response == null) {
            throw new RuntimeException("No response from weather API");
        }

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode current = root.path("current");
            
            if (current.isMissingNode()) {
                JsonNode error = root.path("error");
                if (!error.isMissingNode()) {
                    throw new RuntimeException("API Error: " + error.path("info").asText());
                }
                throw new RuntimeException("Invalid response from weather API");
            }
            
            int temperature = current.path("temperature").asInt();
            return "temperature: " + temperature;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing weather data: " + e.getMessage(), e);
        }
    }
}
