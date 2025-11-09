package org.smarthome.deviceservice.messages;

public record DeactivateDeviceMessage(
        Long id
) implements DeviceMessage {
    public static final DeviceMessageType TYPE = DeviceMessageType.DEACTIVATE;

    @Override
    public DeviceMessageType type() {
        return TYPE;
    }
}
