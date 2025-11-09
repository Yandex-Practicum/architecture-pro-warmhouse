package org.smarthome.deviceservice.device;

import org.smarthome.common.events.DeviceEvent;
import org.smarthome.common.events.DeviceEventType;
import org.smarthome.common.events.DeviceValue;
import org.smarthome.common.model.DeviceStatus;
import org.smarthome.common.model.DeviceType;
import org.smarthome.deviceservice.events.DeviceEventsPublisher;
import org.smarthome.deviceservice.mapper.DeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

@Service
public class DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceEventsPublisher deviceEventsPublisher;

    public Optional<Device> getById(
            Long deviceId
    ) {
        return deviceRepository.findById(deviceId);
    }

    public Optional<Device> getByMacAddress(
            String macAddress
    ) {
        return deviceRepository.findByMacAddress(macAddress);
    }

    public List<Device> listAll() {
        return deviceRepository.findAll();
    }

    public List<Device> listByHouseId(
            Long houseId
    ) {
        return deviceRepository.findByHouseId(houseId);
    }

    public List<Device> listByType(
            DeviceType type
    ) {
        return deviceRepository.findByType(type);
    }

    public List<Device> listByLocation(
            String location
    ) {
        return deviceRepository.findByLocationContainingIgnoreCase(location);
    }

    public List<Device> list(
            @Nullable Long houseId,
            @Nullable DeviceType type,
            @Nullable String location
    ) {
        return deviceRepository.listWithParams(houseId, type, location == null ? "" : location);
    }

    public Device create(
            Device.CreateRequest createRequest
    ) {
        Device savedDevice = deviceRepository.save(DeviceMapper.fromCreateRequest(createRequest));

        publishDeviceEvent(DeviceEventType.CREATED, savedDevice, null);

        return savedDevice;
    }

    public Optional<Device> update(
            long id,
            Device.UpdateRequest updateRequest
    ) {
        return updateInternal(
                id,
                DeviceEventType.UPDATED,
                device -> {
                    device.setName(updateRequest.name());
                    device.setLocation(updateRequest.location());
                    return device;
                });
    }

    public Optional<Long> delete(
            Long deviceId
    ) {
        // XXX Always DEACTIVATE device before deleting to ensure deactivate.event should be sent
        Optional<Device> deactivatedDevice = deactivate(deviceId);
        if (deactivatedDevice.isEmpty())
            return Optional.empty();

        deviceRepository.deleteById(deviceId);

        publishDeviceEvent(DeviceEventType.DELETED, deactivatedDevice.get(), null);

        return Optional.of(deviceId);
    }

    public Optional<Device> activate(
            long id
    ) {
        return updateInternal(
                id,
                DeviceEventType.ACTIVATED,
                device -> {
                    device.setStatus(DeviceStatus.ACTIVE);
                    return device;
                });
    }

    public Optional<Device> deactivate(
            long id
    ) {
        return updateInternal(
                id,
                DeviceEventType.DEACTIVATED,
                device -> {
                    device.setStatus(DeviceStatus.INACTIVE);
                    return device;
                });
    }

    private Optional<Device> updateInternal(
            long id,
            DeviceEventType eventType,
            UnaryOperator<Device> updater
    ) {
        return deviceRepository.findById(id)
                .map(updater)
                .map(device -> {
                    Device savedDevice = deviceRepository.save(device);

                    publishDeviceEvent(eventType, savedDevice, null);

                    return savedDevice;
                });
    }

    public Optional<DeviceValue> setValue(
            long id,
            DeviceValue deviceValue
    ) {
        return deviceRepository.findById(id)
                .map(device -> {
                    if (device.getType() != DeviceType.HEATER) {
                        return null;
                    }

                    publishDeviceEvent(DeviceEventType.SET_VALUE, device, deviceValue);

                    return deviceValue;
                });
    }

    private void publishDeviceEvent(
            DeviceEventType eventType,
            Device device,
            DeviceValue value
    ) {
        deviceEventsPublisher.publish(new DeviceEvent(eventType, DeviceMapper.toDto(device), value));
    }
}
