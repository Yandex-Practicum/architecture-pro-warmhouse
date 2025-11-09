package org.smarthome.telemetryservice.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/metrics")
public class MetricsController {

    @Autowired
    private MetricsService metricsService;

    @GetMapping
    public ResponseEntity<List<DeviceMetricsDto>> list(
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) Long houseId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location,
            @RequestParam(required = false, defaultValue = "10") int limit
    ) {
        if (!ObjectUtils.isEmpty(deviceId)) {
            return metricsService
                    .listByDevice(deviceId, limit)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        return metricsService
                .listByHouseTypeLocation(houseId, type, location, limit)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
