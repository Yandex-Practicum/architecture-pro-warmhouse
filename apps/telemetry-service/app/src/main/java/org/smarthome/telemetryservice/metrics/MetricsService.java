package org.smarthome.telemetryservice.metrics;


import jakarta.validation.constraints.NotNull;
import org.smarthome.common.dto.DeviceDto;
import org.smarthome.telemetryservice.clients.DeviceServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Service
public class MetricsService {
    @Autowired
    private MetricsRepository metricsRepository;
    @Autowired
    private DeviceServiceClient deviceServiceClient;

    public void saveMetricAndHeartbeat(
            @NotNull String macAddress,
            Float value
    ) {
        DeviceDto deviceDto = deviceServiceClient.getDeviceByMacAddress(macAddress);
        if (ObjectUtils.isEmpty(deviceDto)) {
            return;
        }

        if (value != null) {
            metricsRepository.save(new DeviceMetricEntry(deviceDto.id(), value));
        }

        deviceServiceClient.heartbeatDevice(deviceDto.id());
    }

    public Optional<List<DeviceMetricsDto>> listByDevice(
            @NotNull Long deviceId,
            int perDeviceLimit
    ) {
        DeviceDto deviceDto = deviceServiceClient.getDevice(deviceId);
        if (ObjectUtils.isEmpty(deviceDto)) {
            return Optional.empty();
        }

        return Optional.of(List.of(selectDeviceMetrics(deviceDto, perDeviceLimit)));
    }

    public Optional<List<DeviceMetricsDto>> listByHouseTypeLocation(
            @RequestParam(required = false) Long houseId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location,
            int perDeviceLimit
    ) {
        List<DeviceDto> devices = deviceServiceClient.listDevices(houseId, type, location);
        if (devices.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                devices.stream().map(deviceDto -> selectDeviceMetrics(deviceDto, perDeviceLimit)).toList());
    }

    private DeviceMetricsDto selectDeviceMetrics(
            DeviceDto deviceDto,
            int perDeviceLimit
    ) {
        return new DeviceMetricsDto(
                deviceDto,
                metricsRepository.findByDeviceIdOrderByTimestampDesc(deviceDto.id(), Pageable.ofSize(perDeviceLimit)));
    }

    public void deleteByDevice(
            @NotNull Long deviceId
    ) {
        metricsRepository.deleteAllByDeviceId(deviceId);
    }
}
