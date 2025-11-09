package org.smarthome.deviceservice.deviceheartbeat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/heartbeatDevice")
public class DeviceHeartbeatController {

    @Autowired
    private DeviceHeartbeatService deviceHeartbeatService;

    @Operation(summary = "Device heartbeat", description = "Register device heartbeat to indicate it's online")
    @ApiResponse(responseCode = "200", description = "Heartbeat registered successfully")
    @ApiResponse(responseCode = "404", description = "Device not found")
    @PostMapping("/{deviceId}")
    public ResponseEntity<Instant> heartbeat(
            @Parameter(description = "Device ID", required = true)
            @PathVariable Long deviceId
    ) {
        return deviceHeartbeatService
                .heartbeat(deviceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
