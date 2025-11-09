package org.smarthome.deviceservice.deviceheartbeat;

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

    @PostMapping("/{deviceId}")
    public ResponseEntity<Instant> heartbeat(
            @PathVariable Long deviceId
    ) {
        return deviceHeartbeatService
                .heartbeat(deviceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
