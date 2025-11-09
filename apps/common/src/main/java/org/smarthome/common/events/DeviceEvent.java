package org.smarthome.common.events;

import org.smarthome.common.dto.DeviceDto;

public record DeviceEvent(
        DeviceEventType eventType,
        DeviceDto device,
        DeviceValue deviceValue
) {
}
