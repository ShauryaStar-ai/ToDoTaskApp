package com.shaurya.ToDoApp.Controller;

import com.shaurya.ToDoApp.Services.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentWeather(@RequestParam String city) {
        try {
            // Check Redis cache first
            String cachedWeather = redisTemplate.opsForValue().get(city);
            if (cachedWeather != null) {
                System.out.println("Cashe Hit");
                return ResponseEntity.ok(cachedWeather);

            }

            // Fetch from API if not in cache
            String weatherData = weatherService.getCurrentWeather(city);
            
            // Save to Redis with 2 minutes expiration
            redisTemplate.opsForValue().set(city, weatherData, 2, TimeUnit.MINUTES);
            
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching weather data: " + e.getMessage());
        }
    }
}
