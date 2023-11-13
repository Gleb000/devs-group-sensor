package org.devs.group.sensor.dto.measurement;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MeasurementRequest {
    private Double value;
    private Boolean raining;
}
