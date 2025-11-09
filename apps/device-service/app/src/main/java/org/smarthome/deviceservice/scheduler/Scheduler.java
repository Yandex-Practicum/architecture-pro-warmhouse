package org.smarthome.deviceservice.scheduler;

import org.smarthome.deviceservice.deviceheartbeat.DeviceHeartbeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

@EnableScheduling
@Configuration
public class Scheduler {
    @Autowired
    DeviceHeartbeatService deviceHeartbeatService;

    @Scheduled(timeUnit = TimeUnit.SECONDS, fixedDelay = 15)
    public void checkDevicesLivenessHeartbeats() {
        deviceHeartbeatService.checkDevicesLivenessHeartbeats();
    }
}
