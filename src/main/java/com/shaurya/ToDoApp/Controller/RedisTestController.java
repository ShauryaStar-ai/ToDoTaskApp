package com.shaurya.ToDoApp.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis-test")
public class RedisTestController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping
    public String testRedis() {
        try {
            // Set a value
            redisTemplate.opsForValue().set("test_key", "Hello Redis from Spring Boot!");
            
            // Get the value
            String value = redisTemplate.opsForValue().get("test_key");
            
            return "Redis Test Successful! Retrieved value: " + value;
        } catch (Exception e) {
            e.printStackTrace();
            return "Redis Test Failed: " + e.getMessage();
        }
    }
}
