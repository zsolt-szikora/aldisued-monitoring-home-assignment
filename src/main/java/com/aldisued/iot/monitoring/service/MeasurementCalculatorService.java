package com.aldisued.iot.monitoring.service;


import java.util.ArrayList;
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
        .toList();
  }

  public List<Double> getMovingAverage(List<Double> data, int windowSize) {
    requireValidWindowSize(windowSize, data.size());
    List<Double> result = new ArrayList<>();
    // O(n), at the cost of a small accumulated floating-point rounding error
    double movingSum = 0;
    for (int i = 0; i < data.size(); i++) {
      movingSum += data.get(i);
      if (i >= windowSize - 1) {
        result.add(movingSum / windowSize);
        movingSum -= data.get(i - windowSize + 1);
      }
    }
    return List.copyOf(result);
    // a precise and perhaps more readable version - with worse O(n*w) complexity:
    // return
    //  IntStream.rangeClosed(0, data.size() - windowSize).mapToDouble(from ->
    //    IntStream.range(from, from + windowSize).mapToDouble(data::get).average().orElseThrow()).boxed().toList();
  }

  private static void requireValidDeviation(Double deviation) {
    if (deviation == null || !(0 <= deviation && deviation <= 1)) { // negated range check to reject NaN deviation
      throw new IllegalArgumentException("deviation must be within [0.0, 1.0], but was " + deviation);
    }
  }

  private static void requireValidWindowSize(int windowSize, int dataSize) {
    if (windowSize <= 0) {
      throw new IllegalArgumentException("windowSize must be greater than 0");
    }
    if (windowSize > dataSize) {
      throw new IllegalArgumentException("windowSize must not be greater than data size");
    }
  }
}
