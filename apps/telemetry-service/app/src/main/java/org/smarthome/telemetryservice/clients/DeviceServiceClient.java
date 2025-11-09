package org.smarthome.telemetryservice.clients;

import jakarta.validation.constraints.NotNull;
import org.smarthome.common.dto.DeviceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class DeviceServiceClient {
    @Autowired
    private RestClient deviceServiceRestClient;

    public DeviceDto getDevice(
            @NotNull Long deviceId
    ) {
        return deviceServiceRestClient
                .get()
                .uri("/devices/{id}", deviceId)
                .retrieve()
                .body(DeviceDto.class);
    }

    public DeviceDto getDeviceByMacAddress(
            @NotNull String macAddress
    ) {
        return deviceServiceRestClient
                .get()
                .uri("/devices/macAddress/{macAddress}", macAddress)
                .retrieve()
                .body(DeviceDto.class);
    }

    public List<DeviceDto> listDevices(
            Long houseId,
            String type,
            String location
    ) {
        return deviceServiceRestClient
                .get()
                .uri("/devices?houseId={houseId}&type={type}&location={location}", houseId, type, location)
                .retrieve()
                .body(new ParameterizedTypeReference<List<DeviceDto>>() {
                });
    }

    public void unregisterDevice(
            Long deviceId
    ) {
        deviceServiceRestClient
                .delete()
                .uri("/devices/{id}", deviceId)
                .retrieve()
                .toBodilessEntity();
    }

    public void activateDevice(
            Long deviceId
    ) {
        deviceServiceRestClient.
                post()
                .uri("/devices/activate/{id}", deviceId)
                .retrieve()
                .toBodilessEntity();
    }

    public void deactivateDevice(
            Long deviceId
    ) {
        deviceServiceRestClient
                .post()
                .uri("/devices/deactivate/{id}", deviceId)
                .retrieve()
                .toBodilessEntity();
    }

    public void heartbeatDevice(
            Long deviceId
    ) {
        deviceServiceRestClient
                .post()
                .uri("/heartbeatDevice/{id}", deviceId)
                .retrieve()
                .toBodilessEntity();
    }
}
