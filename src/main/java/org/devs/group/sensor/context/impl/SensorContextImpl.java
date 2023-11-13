package org.devs.group.sensor.context.impl;

import org.devs.group.sensor.context.SensorContext;
import org.springframework.stereotype.Component;

@Component
public class SensorContextImpl implements SensorContext {

    private String key;

    @Override
    public void writeKey(String key) {
        this.key = key;
    }

    @Override
    public String retrieveKey() {
        return this.key;
    }
}
