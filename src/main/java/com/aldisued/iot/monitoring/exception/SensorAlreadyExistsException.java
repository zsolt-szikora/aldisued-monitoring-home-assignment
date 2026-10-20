package com.aldisued.iot.monitoring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SensorAlreadyExistsException extends RuntimeException {
  public SensorAlreadyExistsException(String message) {
    super(message);
  }
}
