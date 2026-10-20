package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.entity.Alert;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.exception.AlertNotFoundException;
import com.aldisued.iot.monitoring.exception.SensorNotFoundException;
import com.aldisued.iot.monitoring.repository.AlertRepository;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import java.util.UUID;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AlertService {

  private static final String ALERTS_TOPIC = "alerts";

  private final AlertRepository alertRepository;
  private final SensorRepository sensorRepository;
  private final KafkaTemplate<String, AlertDto> kafkaTemplate;

  public AlertService(AlertRepository alertRepository, SensorRepository sensorRepository,
                      KafkaTemplate<String, AlertDto> kafkaTemplate) {
    this.alertRepository = alertRepository;
    this.sensorRepository = sensorRepository;
    this.kafkaTemplate = kafkaTemplate;
  }

  public Alert saveAlert(AlertDto alertDto) {
    Sensor sensor = requireSensor(alertDto.sensorId());
    Alert alert = alertRepository.save(new Alert(alertDto.message(), alertDto.timestamp(), sensor));
    // fire-and-forget after the DB commit: KafkaTemplate's default LoggingProducerListener logs failed sends.
    // Not atomic with the DB write; a transactional outbox would guarantee delivery.
    kafkaTemplate.send(ALERTS_TOPIC, alertDto);
    return alert;
  }

  public AlertDto findLastAlertBySensorId(UUID sensorId) {
    // uninitialized proxy (id only): lets Hibernate use alerts.sensor_id directly, without a join on sensors
    Sensor sensorProxy = sensorRepository.getReferenceById(sensorId);
    var alert = alertRepository.findFirstBySensorOrderByTimestampDescIdDesc(sensorProxy)
        .orElseThrow(() -> new AlertNotFoundException("Alert not found for sensor: " + sensorId));
    return new AlertDto(alert.getSensor().getId(), alert.getMessage(), alert.getTimestamp());
  }

  private Sensor requireSensor(UUID uuid) {
    return sensorRepository.findById(uuid)
        .orElseThrow(() -> new SensorNotFoundException(uuid));
  }
}
