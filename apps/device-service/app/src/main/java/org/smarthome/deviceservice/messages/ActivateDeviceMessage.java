package org.smarthome.deviceservice.messages;

public record ActivateDeviceMessage(
        Long id
) implements DeviceMessage {
    public static final DeviceMessageType TYPE = DeviceMessageType.ACTIVATE;

    @Override
    public DeviceMessageType type() {
        return TYPE;
    }
}
