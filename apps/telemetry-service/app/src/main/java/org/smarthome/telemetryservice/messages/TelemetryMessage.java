package org.smarthome.telemetryservice.messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
public sealed interface TelemetryMessage permits
        MetricTelemetryMessage {
    TelemetryMessageType type();
}
