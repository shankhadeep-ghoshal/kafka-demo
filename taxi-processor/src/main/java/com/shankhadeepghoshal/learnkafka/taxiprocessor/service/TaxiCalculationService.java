package com.shankhadeepghoshal.learnkafka.taxiprocessor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shankhadeepghoshal.learnkafka.pojos.Coordinate;
import com.shankhadeepghoshal.learnkafka.pojos.Taxi;
import com.shankhadeepghoshal.learnkafka.pojos.TaxiEntity;
import com.shankhadeepghoshal.learnkafka.taxiprocessor.utils.TaxiDistanceCalculatorUtils;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxiCalculationService {
  private static final Double ZERO_VALUE = 0.0;

  public String calculateDriverDistance(
      final Taxi taxi,
      final Function<Long, String> checkTaxiExistsFunction,
      final BiFunction<Long, String, String> putTaxiInPersistenceFunction,
      final ObjectMapper objectMapper)
      throws IOException {
    final var currentTaxiRawData = checkTaxiExistsFunction.apply(taxi.id());
    log.info("Data got from redis {}", currentTaxiRawData);
    final String taxiEntityForPersistence;

    if (null != currentTaxiRawData) {
      taxiEntityForPersistence = handleTaxiInPersistence(taxi, objectMapper, currentTaxiRawData);
    } else {
      taxiEntityForPersistence = handleTaxiNotInPersistence(taxi, objectMapper);
    }

    return putTaxiInPersistenceFunction.apply(taxi.id(), taxiEntityForPersistence);
  }

  private static String handleTaxiInPersistence(
      final Taxi taxi, final ObjectMapper objectMapper, final String currentTaxiRawData)
      throws JsonProcessingException {
    final var previousTaxiEntity =
        objectMapper.readValue(currentTaxiRawData, new TypeReference<TaxiEntity>() {});
    final var previousCoordinates =
        new Coordinate(previousTaxiEntity.latitude(), previousTaxiEntity.longitude());
    final var currentCoordinates = new Coordinate(taxi.latitude(), taxi.longitude());
    final var distanceTravelled =
        TaxiDistanceCalculatorUtils.calculateTotalDistanceTravelled(
            previousCoordinates, currentCoordinates, previousTaxiEntity.totalDistance());
    final var currentTaxiEntity =
        new TaxiEntity(taxi.latitude(), taxi.longitude(), distanceTravelled);

    return objectMapper.writeValueAsString(currentTaxiEntity);
  }

  private static String handleTaxiNotInPersistence(final Taxi taxi, final ObjectMapper objectMapper)
      throws JsonProcessingException {
    final var firstTimeTaxi = new TaxiEntity(taxi.latitude(), taxi.longitude(), ZERO_VALUE);

    return objectMapper.writeValueAsString(firstTimeTaxi);
  }
}
