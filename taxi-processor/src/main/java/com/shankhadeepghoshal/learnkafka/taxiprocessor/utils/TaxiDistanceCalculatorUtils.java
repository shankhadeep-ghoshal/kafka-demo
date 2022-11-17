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
    return previousDistance + calculateDistance(previous, current);
  }

  public static Double calculateDistance(final Coordinate previous, final Coordinate current) {
    return Math.sqrt(squared(current.x() - previous.x()) + squared(current.y() - previous.y()));
  }

  public static Double squared(final Double a) {
    return Math.pow(a, 2);
  }
}
