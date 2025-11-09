package org.smarthome.deviceservice.deviceheartbeat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "device_heartbeats")
public class DeviceHeartbeat {
    @Id
    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "heartbeat_at")
    private Instant heartbeatAt;

    public DeviceHeartbeat() {
    }

    public DeviceHeartbeat(Long deviceId, Instant heartbeatAt) {
        this.deviceId = deviceId;
        this.heartbeatAt = heartbeatAt;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Instant getHeartbeatAt() {
        return heartbeatAt;
    }

    public void setHeartbeatAt(Instant heartbeatAt) {
        this.heartbeatAt = heartbeatAt;
    }
}
