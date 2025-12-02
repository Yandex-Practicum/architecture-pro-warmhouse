package com.warmhouse.temperatureapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
public class TemperatureController {

    private final Random random = new Random();

    @GetMapping("/temperature")
    public Map<String, Object> getTemperature(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String sensorId) {

        // If no location is provided, use a default based on sensor ID
        if ((location == null || location.isEmpty()) && sensorId != null && !sensorId.isEmpty()) {
            location = switch (sensorId) {
                case "1" -> "Living Room";
                case "2" -> "Bedroom";
                case "3" -> "Kitchen";
                default -> "Unknown";
            };
        }

        // If no sensor ID is provided, generate one based on location
        if ((sensorId == null || sensorId.isEmpty()) && location != null && !location.isEmpty()) {
            sensorId = switch (location) {
                case "Living Room" -> "1";
                case "Bedroom" -> "2";
                case "Kitchen" -> "3";
                default -> "0";
            };
        }

        // Default values if both are empty
        if (location == null || location.isEmpty()) {
            location = "Unknown";
        }
        if (sensorId == null || sensorId.isEmpty()) {
            sensorId = "0";
        }

        // Generate random temperature between 18 and 26 degrees Celsius
        double temperature = 18 + (26 - 18) * random.nextDouble();

        Map<String, Object> response = new HashMap<>();
        response.put("sensorId", sensorId);
        response.put("location", location);
        response.put("temperature", Math.round(temperature * 100.0) / 100.0);
        response.put("unit", "Celsius");

        return response;
    }
}

