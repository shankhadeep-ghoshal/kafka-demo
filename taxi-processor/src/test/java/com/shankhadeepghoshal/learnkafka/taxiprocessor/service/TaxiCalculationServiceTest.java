package com.shankhadeepghoshal.learnkafka.taxiprocessor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shankhadeepghoshal.learnkafka.pojos.Taxi;
import com.shankhadeepghoshal.learnkafka.pojos.TaxiEntity;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaxiCalculationServiceTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
  void calculateDistanceWithPreviousTrip() throws IOException {
    final var taxiCalculatorService = new TaxiCalculationService();
    final var taxi = new Taxi(1L, 50.0, 50.0);
    final Function<Long, String> taxiEntityJsonString =
        taxiId -> {
          try {
            return OBJECT_MAPPER.writeValueAsString(new TaxiEntity(60.0, 60.0, 100.0));
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        };
    final var expectedResult = OBJECT_MAPPER.writeValueAsString(new TaxiEntity(60.0, 60.0, 114.14));
    final BiFunction<Long, String, String> finishedTaxiEntity = (taxiId, data) -> expectedResult;
    final var actualResult =
        taxiCalculatorService.calculateDriverDistance(
            taxi, taxiEntityJsonString, finishedTaxiEntity, OBJECT_MAPPER);

    Assertions.assertEquals(expectedResult, actualResult);
  }

  @Test
  void calculateDistanceForFirstTime() throws IOException {
      final var taxiCalculatorService = new TaxiCalculationService();
      final var taxi = new Taxi(1L, 50.0, 50.0);
      final Function<Long, String> emptyTaxiEntity = taxiId -> null;
      final var expectedResult = OBJECT_MAPPER.writeValueAsString(new TaxiEntity(taxi.latitude(), taxi.longitude(), 0.0));
      final BiFunction<Long, String, String> finishedTaxiEntity = (taxiId, data) -> expectedResult;
      final var actualResult = taxiCalculatorService.calculateDriverDistance(taxi,
              emptyTaxiEntity,
              finishedTaxiEntity,
              OBJECT_MAPPER);

      Assertions.assertEquals(expectedResult, actualResult);
    }
}
