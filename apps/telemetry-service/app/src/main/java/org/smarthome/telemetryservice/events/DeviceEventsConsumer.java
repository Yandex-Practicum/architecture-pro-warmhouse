package org.smarthome.telemetryservice.events;

import org.smarthome.common.events.DeviceEvent;
import org.smarthome.common.events.DeviceEventType;
import org.smarthome.telemetryservice.metrics.MetricsService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.smarthome.common.constants.Constants.DEVICE_EVENTS_TOPIC_EXCHANGE_NAME;


@Component
public class DeviceEventsConsumer {
    public final static String DEVICE_EVENTS_TOPIC_TELEMETRY_SERVICE_CONSUMER = "smart-home.device2telemetry.events";
    @Autowired
    private MetricsService metricsService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = DEVICE_EVENTS_TOPIC_TELEMETRY_SERVICE_CONSUMER),
                    exchange = @Exchange(value = DEVICE_EVENTS_TOPIC_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
                    key = "*"
            ),
            messageConverter = "jsonMessageConverter")
    public void processMessage(
            DeviceEvent deviceEvent
    ) {
        try {
            switch (deviceEvent.eventType()) {
                case DeviceEventType.DELETED -> metricsService.deleteByDevice(deviceEvent.device().id());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
