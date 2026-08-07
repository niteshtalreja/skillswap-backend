package com.skillswap.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/health")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        log.info("Health check endpoint called");
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "SkillSwap Backend");
        response.put("timestamp", System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
