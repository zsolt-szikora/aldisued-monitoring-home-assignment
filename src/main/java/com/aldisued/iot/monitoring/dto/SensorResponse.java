package com.aldisued.iot.monitoring.dto;

import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.entity.SensorType;
import java.util.UUID;

public record SensorResponse(
    UUID id,
    String name,
    SensorType type) {

  public static SensorResponse from(Sensor sensor) {
    return new SensorResponse(
        sensor.getId(),
        sensor.getName(),
        sensor.getType()
    );
  }
}
