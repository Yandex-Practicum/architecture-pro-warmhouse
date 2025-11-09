package org.smarthome.telemetryservice.metrics;

import org.smarthome.common.dto.DeviceDto;

import java.util.List;

public record DeviceMetricsDto(
        DeviceDto device,
        List<DeviceMetricEntry> metrics
) {
}
