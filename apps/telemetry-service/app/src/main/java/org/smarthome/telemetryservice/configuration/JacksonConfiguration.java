package org.smarthome.telemetryservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.smarthome.common.configuration.BaseJacksonConfiguration;
import org.smarthome.telemetryservice.messages.MetricTelemetryMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration extends BaseJacksonConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = baseObjectMapper();

        objectMapper.registerSubtypes(
                new NamedType(MetricTelemetryMessage.class, MetricTelemetryMessage.TYPE.name()));

        return objectMapper;
    }
}