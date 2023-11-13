package org.devs.group.sensor.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devs.group.sensor.context.SensorContext;
import org.devs.group.sensor.dto.sensor.RegisterSensorRequest;
import org.devs.group.sensor.dto.sensor.RegisterSensorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class SystemStartupListener {

    private static final String REGISTRATION_URL_POSTFIX = "/sensors/registration";

    @Value("${sensor.name}")
    private String sensorName;

    @Value("${sensor.server.url}")
    private String serverUrl;

    private final RestTemplate restTemplate;
    private final SensorContext sensorContext;

    @EventListener(ApplicationReadyEvent.class)
    public void registerSensorAfterStartup() {
        String url = serverUrl + REGISTRATION_URL_POSTFIX;
        RegisterSensorRequest request = RegisterSensorRequest.builder()
                .name(sensorName)
                .build();

        HttpEntity<RegisterSensorRequest> requestEntity = new HttpEntity<>(request);

        ResponseEntity<RegisterSensorResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        if (response.getStatusCode() == HttpStatus.CREATED) {
            if (response.getBody() == null) {
                log.error("**********\nResponse body is empty after registering\n**********");

                System.exit(-1);
            }

            RegisterSensorResponse responseBody = response.getBody();

            if (responseBody.getKey() == null) {
                log.error("**********\nReturned key is empty\n**********");

                System.exit(-1);
            }

            log.info("Sensor was successfully registered");

            sensorContext.writeKey(responseBody.getKey());
        } else if (response.getStatusCode() == HttpStatus.CONFLICT) {
            log.error("**********\nSensor with this name is already exist\n**********");

            System.exit(-1);
        } else {
            log.error("**********\nCritical server error\n**********");

            System.exit(-1);
        }
    }
}
