package com.smarthome.telemetry.repository;

import com.smarthome.telemetry.model.TelemetryReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelemetryRepository extends JpaRepository<TelemetryReading, Long> {
    List<TelemetryReading> findBySensorIdOrderByTimestampDesc(Integer sensorId);
    List<TelemetryReading> findAllByOrderByTimestampDesc();
}
