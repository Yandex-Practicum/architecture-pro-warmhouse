package org.smarthome.deviceservice.mapper;

import org.smarthome.common.dto.DeviceDto;
import org.smarthome.deviceservice.device.Device;
import org.smarthome.deviceservice.messages.RegisterNewDeviceMessage;

public class DeviceMapper {
    public static Device fromCreateRequest(
            Device.CreateRequest createRequest
    ) {
        return new Device(
                createRequest.houseId(),
                createRequest.name(),
                createRequest.deviceType(),
                createRequest.location(),
                createRequest.macAddress()
        );
    }

    public static DeviceDto toDto(
            Device device
    ) {
        return new DeviceDto(
                device.getId(),
                device.getHouseId(),
                device.getName(),
                device.getType(),
                device.getLocation(),
                device.getMacAddress(),
                device.getStatus(),
                device.getCreatedAt(),
                device.getUpdatedAt()
        );
    }

    public static Device.CreateRequest createRequestFromRegisterNewDeviceMessage(
            RegisterNewDeviceMessage registerNewDeviceMessage
    ) {
        return new Device.CreateRequest(
                registerNewDeviceMessage.houseId(),
                registerNewDeviceMessage.name(),
                registerNewDeviceMessage.deviceType(),
                registerNewDeviceMessage.location(),
                registerNewDeviceMessage.macAddress()
        );
    }
}
