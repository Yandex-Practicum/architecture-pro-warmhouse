package org.smarthome.deviceservice.messages;

import org.smarthome.deviceservice.device.DeviceService;
import org.smarthome.deviceservice.mapper.DeviceMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.smarthome.common.constants.Constants.DEVICE_COMMANDS_QUEUE;

@Component
public class DeviceMessagesConsumer {
    @Autowired
    private DeviceService deviceService;

    @RabbitListener(
            queues = DEVICE_COMMANDS_QUEUE,
            messageConverter = "jsonMessageConverter")
    public void processMessage(
            DeviceMessage deviceMessage
    ) {
        try {
            switch (deviceMessage) {
                case RegisterNewDeviceMessage registerNewDeviceMessage ->
                        deviceService.create(DeviceMapper.createRequestFromRegisterNewDeviceMessage(registerNewDeviceMessage));
                case UnregisterDeviceMessage(Long deviceId) -> deviceService.delete(deviceId);
                case ActivateDeviceMessage(Long deviceId) -> deviceService.activate(deviceId);
                case DeactivateDeviceMessage(Long deviceId) -> deviceService.deactivate(deviceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
