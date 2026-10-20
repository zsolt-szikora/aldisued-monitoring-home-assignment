package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.dto.SensorDto;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.exception.SensorAlreadyExistsException;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class SensorService {

  private final SensorRepository sensorRepository;

  public SensorService(SensorRepository sensorRepository) {
    this.sensorRepository = sensorRepository;
  }

  public Sensor saveSensor(SensorDto sensor) {
    if (sensorRepository.existsByName(sensor.name())) {
      throw new SensorAlreadyExistsException(sensor.name());
    }
    try {
      return sensorRepository.saveAndFlush(new Sensor(
          sensor.name(),
          sensor.type()
      ));
    } catch (DataIntegrityViolationException e) {
      // race: another request inserted the same name after our existsByName check
      if (isNameUniqueViolation(e)) {
        throw new SensorAlreadyExistsException(sensor.name());
      }
      throw e;
    }
  }

  private static boolean isNameUniqueViolation(DataIntegrityViolationException e) {
    return e.getCause() instanceof ConstraintViolationException cve
        && Sensor.UK_NAME.equalsIgnoreCase(cve.getConstraintName());
  }
}
