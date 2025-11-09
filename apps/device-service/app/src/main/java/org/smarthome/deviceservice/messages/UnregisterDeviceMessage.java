package org.smarthome.deviceservice.messages;

public record UnregisterDeviceMessage(
        Long id
) implements DeviceMessage {
    public static final DeviceMessageType TYPE = DeviceMessageType.UNREGISTER;

    @Override
    public DeviceMessageType type() {
        return TYPE;
    }
}
