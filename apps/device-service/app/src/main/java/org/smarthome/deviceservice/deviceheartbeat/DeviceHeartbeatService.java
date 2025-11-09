package org.smarthome.deviceservice.deviceheartbeat;

import org.smarthome.common.model.DeviceStatus;
import org.smarthome.deviceservice.device.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class DeviceHeartbeatService {
    @Autowired
    private DeviceHeartbeatRepository deviceHeartbeatRepository;
    @Autowired
    private DeviceService deviceService;

    public Optional<Instant> heartbeat(
            Long deviceId
    ) {
        return deviceService
                .getById(deviceId)
                .map(device -> {
                    Instant heartbeatAt = deviceHeartbeatRepository
                            .save(new DeviceHeartbeat(deviceId, Instant.now()))
                            .getHeartbeatAt();

                    if (device.getStatus() != DeviceStatus.ACTIVE) {
                        // При получении хертбита помечаем устройство активным.
                        deviceService.activate(deviceId);
                    }

                    return heartbeatAt;
                });
    }

    public void checkDevicesLivenessHeartbeats() {
        // Помечаем неактивными устройства, от которых не получали хертбиты более 10 минут.
        int deactivatedByHeartbeatDeviceCount = deviceHeartbeatRepository.deactivateDevicesByFailedHeartbeats(
                DeviceStatus.INACTIVE.name(),
                Instant.now().minus(10, ChronoUnit.MINUTES));
        System.out.printf("Deactivated by heartbeat device count: %d\n", deactivatedByHeartbeatDeviceCount);
    }
}
