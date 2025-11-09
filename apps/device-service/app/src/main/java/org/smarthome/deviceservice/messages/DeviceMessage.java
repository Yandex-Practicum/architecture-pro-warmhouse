package org.smarthome.deviceservice.messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
public sealed interface DeviceMessage permits
        RegisterNewDeviceMessage,
        UnregisterDeviceMessage,
        ActivateDeviceMessage,
        DeactivateDeviceMessage {
    DeviceMessageType type();
}
