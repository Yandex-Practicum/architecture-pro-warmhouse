package org.smarthome.telemetryservice.clients;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "clients.device-service")
public record DeviceServiceClientConfig(
        String url
) {
}
