package org.devs.group.sensor.dto.sensor;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterSensorRequest {
    private String name;
}
