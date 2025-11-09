package org.smarthome.telemetryservice.metrics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "Get all metrics", description = "Retrieve metrics for ALL registered devices")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved device metrics")
    @GetMapping
    public ResponseEntity<List<DeviceMetricsDto>> list(
            @Parameter(description = "Filter metrics by device ID")
            @RequestParam(required = false) Long deviceId,
            @Parameter(description = "Filter metrics by house ID")
            @RequestParam(required = false) Long houseId,
            @Parameter(description = "Filter metrics by device type")
            @RequestParam(required = false) String type,
            @Parameter(description = "Filter metrics by device location")
            @RequestParam(required = false) String location,
            @Parameter(description = "Limit count of received metrics per one device")
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
