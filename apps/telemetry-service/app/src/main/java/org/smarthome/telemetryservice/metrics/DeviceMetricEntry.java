package org.smarthome.telemetryservice.metrics;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "metrics")
public class DeviceMetricEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Long deviceId;

    @Column(nullable = false)
    private Instant timestamp;

    @Column
    private float value;

    public DeviceMetricEntry() {
    }

    public DeviceMetricEntry(
            Long deviceId,
            float value
    ) {
        this.deviceId = deviceId;
        this.value = value;
        this.timestamp = Instant.EPOCH;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @PreUpdate
    public void preUpdate() {
        this.timestamp = Instant.now();
    }
}
