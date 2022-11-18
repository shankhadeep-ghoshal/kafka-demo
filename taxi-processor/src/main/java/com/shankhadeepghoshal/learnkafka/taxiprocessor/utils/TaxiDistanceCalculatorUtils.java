package com.shankhadeepghoshal.learnkafka.taxiprocessor.utils;

import com.shankhadeepghoshal.learnkafka.pojos.Coordinate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxiDistanceCalculatorUtils {
  public static Double calculateTotalDistanceTravelled(
      final Coordinate previous, final Coordinate current, final Double previousDistance) {
    double v = previousDistance + calculateDistance(previous, current);
    return v;
  }

  public static Double calculateDistance(final Coordinate previous, final Coordinate current) {
    double sqrt =
        Math.sqrt(squared(current.x() - previous.x()) + squared(current.y() - previous.y()));
    return sqrt;
  }

  public static Double squared(final Double a) {
    return Math.pow(a, 2);
  }
}
