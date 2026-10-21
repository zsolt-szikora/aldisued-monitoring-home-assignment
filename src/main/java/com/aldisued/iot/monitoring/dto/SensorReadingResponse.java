package com.aldisued.iot.monitoring.dto;

import com.aldisued.iot.monitoring.entity.SensorReading;
import java.time.LocalDateTime;
import java.util.UUID;

public record SensorReadingResponse(
    Long id,
    UUID sensorId,
    Double value,
    LocalDateTime timestamp
) {

  public static SensorReadingResponse from(SensorReading reading) {
    return new SensorReadingResponse(
        reading.getId(),
        reading.getSensor().getId(),
        reading.getValue(),
        reading.getTimestamp()
    );
  }
}
