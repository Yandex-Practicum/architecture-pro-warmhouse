package org.smarthome.common.dto;

import org.smarthome.common.model.DeviceStatus;
import org.smarthome.common.model.DeviceType;

import java.time.Instant;

public record DeviceDto(
        Long id,
        Long houseId,
        String name,
        DeviceType type,
        String location,
        String macAddress,
        DeviceStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
