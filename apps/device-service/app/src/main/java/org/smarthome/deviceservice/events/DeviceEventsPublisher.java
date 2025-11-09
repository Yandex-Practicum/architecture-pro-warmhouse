package org.smarthome.deviceservice.events;

import org.smarthome.common.constants.Constants;
import org.smarthome.common.events.DeviceEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeviceEventsPublisher {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publish(
            DeviceEvent deviceEvent
    ) {
        rabbitTemplate.convertAndSend(
                Constants.DEVICE_EVENTS_TOPIC_EXCHANGE_NAME,
                deviceEvent.device().macAddress(), // используем MacAddress в качестве RoutingKey(BindingKey) у для слушателей топика
                deviceEvent
        );
    }
}
