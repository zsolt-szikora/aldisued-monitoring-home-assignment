package com.aldisued.iot.monitoring.controller;

import com.aldisued.iot.monitoring.dto.SensorReadingDto;
import com.aldisued.iot.monitoring.dto.SensorReadingResponse;
import com.aldisued.iot.monitoring.service.SensorReadingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sensor-readings")
public class SensorReadingController {

  private final SensorReadingService sensorReadingService;

  public SensorReadingController(SensorReadingService sensorReadingService) {
    this.sensorReadingService = sensorReadingService;
  }

  @PostMapping
  public SensorReadingResponse saveSensorReading(@RequestBody SensorReadingDto sensorReadingDto) {
    return SensorReadingResponse.from(sensorReadingService.saveSensorReading(sensorReadingDto));
  }
}
