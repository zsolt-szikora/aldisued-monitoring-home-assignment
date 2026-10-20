package com.aldisued.iot.monitoring.service;


import java.util.List;
import java.util.OptionalDouble;
import org.springframework.stereotype.Service;

@Service
public class MeasurementCalculatorService {

  public List<Double> filterByAverageDeviation(List<Double> values, Double deviation) {
    requireValidDeviation(deviation);
    OptionalDouble avg = values.stream().mapToDouble(Double::doubleValue).average();
    if (avg.isEmpty()) {
      return List.of();
    }
    double avgValue = avg.getAsDouble();
    double tolerance = deviation * Math.abs(avgValue);
    return values.stream()
        .filter(v -> Math.abs(v - avgValue) <= tolerance)
        .toList();  }

  public List<Double> getMovingAverage(List<Double> data, int windowSize) {
    // TODO: Task 10
    return List.of();
  }

  private static void requireValidDeviation(Double deviation) {
    if (deviation == null || !(0 <= deviation && deviation <= 1)) { // negated range check to reject NaN deviation
      throw new IllegalArgumentException("deviation must be within [0.0, 1.0], but was " + deviation);
    }
  }

}
