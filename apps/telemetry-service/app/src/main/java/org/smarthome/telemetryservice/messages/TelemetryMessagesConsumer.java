package org.smarthome.telemetryservice.messages;

import org.smarthome.telemetryservice.metrics.MetricsService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.smarthome.common.constants.Constants.TELEMETRY_METRICS_QUEUE;

@Component
public class TelemetryMessagesConsumer {
    @Autowired
    private MetricsService metricsService;

    @RabbitListener(
            queues = TELEMETRY_METRICS_QUEUE,
            messageConverter = "jsonMessageConverter")
    public void processMessage(
            TelemetryMessage telemetryMessage
    ) {
        try {
            switch (telemetryMessage) {
                case MetricTelemetryMessage(String macAddress, Float value) ->
                        metricsService.saveMetricAndHeartbeat(macAddress, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
