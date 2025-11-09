package org.smarthome.deviceservice.messages;

import org.smarthome.common.model.DeviceType;

public record RegisterNewDeviceMessage(
        Long houseId,
        String name,
        DeviceType deviceType,
        String location,
        String macAddress
) implements DeviceMessage {
    public static final DeviceMessageType TYPE = DeviceMessageType.REGISTER;

    @Override
    public DeviceMessageType type() {
        return TYPE;
    }
}
