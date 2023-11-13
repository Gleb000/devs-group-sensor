package org.devs.group.sensor.config.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devs.group.sensor.context.SensorContext;
import org.devs.group.sensor.dto.measurement.MeasurementRequest;
import org.devs.group.sensor.dto.sensor.RegisterSensorRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class MeasurementSchedulerConfig {

    @Value("${sensor.server.url}")
    private String serverUrl;

    private Instant lastMeasurement = Instant.now();

    private static final String REGISTRATION_URL_POSTFIX = "/sensors/%s/measurements";
    private static final long SECOND_IN_MILLIS = 1000;
    private static final long MIN_LIMIT_TIMER = 3;
    private static final long MAX_LIMIT_TIMER = 15;
    private static final double MIN_MEASUREMENT_VALUE = -100;
    private static final double MAX_MEASUREMENT_VALUE = 100;
    private static final int MEASUREMENT_CHANCE = 2;
    private static final double MEASUREMENT_VALUE_SYMBOLS_LIMIT = 10.0;

    private final RestTemplate restTemplate;
    private final SensorContext sensorContext;

    @Scheduled(fixedRate = SECOND_IN_MILLIS)
    public void scheduleMeasurements() {
        Instant now = Instant.now();

        if (now.minusSeconds(MAX_LIMIT_TIMER).isAfter(lastMeasurement)) {
            sendMeasurements(generateRandomMeasurement(), now);
        } else if (now.minusSeconds(MIN_LIMIT_TIMER).isAfter(lastMeasurement)) {
            if (isSuccess()) {
                sendMeasurements( generateRandomMeasurement(), now);
            }
        }
    }

    private void sendMeasurements(MeasurementRequest measurementRequest, Instant measurementDate) {
        String url = String.format(serverUrl + REGISTRATION_URL_POSTFIX, sensorContext.retrieveKey());

        HttpEntity<MeasurementRequest> requestEntity = new HttpEntity<>(measurementRequest);

        ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        if (response.getStatusCode() != HttpStatus.CREATED) {
            log.error("**********\nCritical server error\n**********");

            System.exit(-1);
        }

        log.info("Measurements was sent to server");

        lastMeasurement = measurementDate;
    }

    private MeasurementRequest generateRandomMeasurement() {
        double value = MIN_MEASUREMENT_VALUE + Math.random() * MAX_MEASUREMENT_VALUE;
        double normalizedValue =
                Math.round(value*MEASUREMENT_VALUE_SYMBOLS_LIMIT)/MEASUREMENT_VALUE_SYMBOLS_LIMIT;

        boolean raining = isSuccess();

        return MeasurementRequest.builder()
                .value(normalizedValue)
                .raining(raining)
                .build();
    }

    private boolean isSuccess() {
        int value = (int) (Math.random() * MEASUREMENT_CHANCE);

        return value > 0 ? Boolean.TRUE : Boolean.FALSE;
    }
}
