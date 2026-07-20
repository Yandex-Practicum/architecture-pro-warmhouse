package com.smarthome.telemetry.controller;

import com.smarthome.telemetry.model.TelemetryReading;
import com.smarthome.telemetry.repository.TelemetryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/telemetry")
public class TelemetryController {

    private final TelemetryRepository repository;

    public TelemetryController(TelemetryRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<TelemetryReading> createReading(@RequestBody TelemetryReading reading) {
        TelemetryReading saved = repository.save(reading);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<TelemetryReading>> getAllReadings() {
        List<TelemetryReading> readings = repository.findAllByOrderByTimestampDesc();
        return ResponseEntity.ok(readings);
    }

    @GetMapping("/sensor/{sensorId}")
    public ResponseEntity<List<TelemetryReading>> getReadingsBySensor(@PathVariable Integer sensorId) {
        List<TelemetryReading> readings = repository.findBySensorIdOrderByTimestampDesc(sensorId);
        return ResponseEntity.ok(readings);
    }

    @GetMapping("/sensor/{sensorId}/latest")
    public ResponseEntity<TelemetryReading> getLatestReading(@PathVariable Integer sensorId) {
        List<TelemetryReading> readings = repository.findBySensorIdOrderByTimestampDesc(sensorId);
        if (readings.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(readings.get(0));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteReading(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Reading deleted successfully"));
    }
}
