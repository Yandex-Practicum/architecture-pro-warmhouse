package org.smarthome.telemetryservice.clients;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration()
public class DeviceServiceClientConfiguration {
    @Bean
    RestClient deviceServiceRestClient(
            DeviceServiceClientConfig config
    ) {
        return RestClient.builder()
                .baseUrl(config.url())
//                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
//                    // Do nothing here to prevent the RestClientException from being thrown.
//                    // You can log the error, process the response body, etc., without throwing.
//                    System.out.println("Error Status Code: " + response.getStatusCode());
//                    System.out.println("Error Response Body: " + response.getBody());
//                })
                .build();
    }
}
