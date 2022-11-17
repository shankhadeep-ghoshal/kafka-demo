package com.shankhadeepghoshal.learnkafka.taxiprocessor.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shankhadeepghoshal.learnkafka.pojos.Coordinate;
import com.shankhadeepghoshal.learnkafka.pojos.Taxi;
import com.shankhadeepghoshal.learnkafka.taxiprocessor.pojos.TaxiEntity;
import com.shankhadeepghoshal.learnkafka.taxiprocessor.utils.TaxiDistanceCalculatorUtils;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
  GenericObjectPool<StatefulRedisConnection<String, String>> pool;
  ObjectMapper objectMapper;

  public String handleRedisStuff(final Taxi taxi)
      throws Exception { // The Library throws Exception, not me
    log.info("Taxi data from REDIS lookup {}", taxi);
    try (final var connection = pool.borrowObject()) {
      final var commands = connection.sync();
      final var currentTaxiRawData = commands.get(String.valueOf(taxi.id()));
      log.info("Data got from redis {}", currentTaxiRawData);
      final String taxiEntityForPersistence;

      if (null != currentTaxiRawData) {
        final var previousTaxiEntity =
            objectMapper.readValue(currentTaxiRawData, new TypeReference<TaxiEntity>() {});
        final var previousCoordinates =
            new Coordinate(previousTaxiEntity.latitude(), previousTaxiEntity.longitude());
        final var currentCoordinates =
            new Coordinate(previousTaxiEntity.latitude(), previousTaxiEntity.longitude());
        final var distanceTravelled =
            TaxiDistanceCalculatorUtils.calculateTotalDistanceTravelled(
                previousCoordinates,
                currentCoordinates,
                previousTaxiEntity.previousTotalDistance());
        final var currentTaxiEntity =
            new TaxiEntity(taxi.latitude(), taxi.longitude(), distanceTravelled);
        taxiEntityForPersistence = objectMapper.writeValueAsString(currentTaxiEntity);

      } else {
        final var firstTimeTaxi = new TaxiEntity(taxi.latitude(), taxi.longitude(), 0.0);
        taxiEntityForPersistence = objectMapper.writeValueAsString(firstTimeTaxi);
      }
      return commands.set(String.valueOf(taxi.id()), taxiEntityForPersistence);
    }
  }

  public TaxiEntity getTaxiEntityById(final Long id) throws Exception {
    try (final var connection = pool.borrowObject()) {
      final var commands = connection.sync();
      final var rawTaxiEntity = commands.get(String.valueOf(id));
      return objectMapper.readValue(rawTaxiEntity, new TypeReference<>() {});
    }
  }
}
