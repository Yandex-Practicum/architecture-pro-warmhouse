package org.smarthome.telemetryservice.messages;

public record MetricTelemetryMessage(
        String macAddress,
        Float value
) implements TelemetryMessage {
    public static final TelemetryMessageType TYPE = TelemetryMessageType.METRIC;

    @Override
    public TelemetryMessageType type() {
        return TYPE;
    }
}
