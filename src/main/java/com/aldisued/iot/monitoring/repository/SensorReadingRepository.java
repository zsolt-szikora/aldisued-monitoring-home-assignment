package com.aldisued.iot.monitoring.repository;

import com.aldisued.iot.monitoring.entity.SensorReading;
import com.aldisued.iot.monitoring.entity.SensorType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

  // JPA query needed because Spring Data's method name parser does not allow returning a calculated column
  @Query("""
      SELECT AVG(r.value)
      FROM SensorReading r
      JOIN r.sensor s
      WHERE s.type = :type
        AND r.timestamp BETWEEN :from AND :to
      """)
  Double findAverageValueBySensorType(
      @Param("type") SensorType sensorType,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to);

  // JPQL to avoid Spring Data projection which cannot return List<Double> directly
  @Query("""
      SELECT r.value
      FROM SensorReading r
      JOIN r.sensor s
      WHERE s.type = :type
        AND r.timestamp BETWEEN :from AND :to
      ORDER BY r.timestamp, r.id
      """)
  List<Double> findValuesBySensorType(
      @Param("type") SensorType sensorType,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to);
}
