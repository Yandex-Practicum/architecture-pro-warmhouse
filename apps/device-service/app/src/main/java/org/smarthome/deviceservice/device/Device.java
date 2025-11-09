package org.smarthome.deviceservice.device;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.smarthome.common.model.DeviceStatus;
import org.smarthome.common.model.DeviceType;

import java.time.Instant;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "House id is required")
    @Column(name = "house_id")
    private Long houseId;

    @NotBlank(message = "Device name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull(message = "Device type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType type;

    @NotBlank(message = "Location is required")
    @Column(nullable = false, length = 100)
    private String location;

    @NotBlank(message = "MacAddress is required")
    @Column(name = "mac_address", nullable = false, length = 100)
    private String macAddress;

    @NotNull(message = "Device status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Device() {
    }

    public Device(
            Long houseId,
            String name,
            DeviceType type,
            String location,
            String macAddress
    ) {
        this.id = null;
        this.houseId = houseId;
        this.name = name;
        this.type = type;
        this.location = location;
        this.macAddress = macAddress;
        this.status = DeviceStatus.ACTIVE;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public DeviceStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceStatus status) {
        this.status = status;
    }

    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public record CreateRequest(
            @NotNull(message = "houseId is required")
            Long houseId,

            @NotBlank(message = "name is required")
            String name,

            @NotNull(message = "deviceType is required")
            @Enumerated(EnumType.STRING)
            @Valid DeviceType deviceType,

            @NotBlank(message = "location is required")
            String location,

            @NotBlank(message = "location is required")
            String macAddress
    ) {
    }

    public record UpdateRequest(
            @NotBlank(message = "name is required")
            String name,

            @NotBlank(message = "location is required")
            String location
    ) {
    }
}