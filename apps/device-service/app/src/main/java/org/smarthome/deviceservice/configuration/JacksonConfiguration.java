package org.smarthome.deviceservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.smarthome.common.configuration.BaseJacksonConfiguration;
import org.smarthome.deviceservice.messages.ActivateDeviceMessage;
import org.smarthome.deviceservice.messages.DeactivateDeviceMessage;
import org.smarthome.deviceservice.messages.RegisterNewDeviceMessage;
import org.smarthome.deviceservice.messages.UnregisterDeviceMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration extends BaseJacksonConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = baseObjectMapper();

        objectMapper.registerSubtypes(
                new NamedType(RegisterNewDeviceMessage.class, RegisterNewDeviceMessage.TYPE.name()),
                new NamedType(UnregisterDeviceMessage.class, UnregisterDeviceMessage.TYPE.name()),
                new NamedType(ActivateDeviceMessage.class, ActivateDeviceMessage.TYPE.name()),
                new NamedType(DeactivateDeviceMessage.class, DeactivateDeviceMessage.TYPE.name()));

        return objectMapper;
    }
}