package com.shankhadeepghoshal.learnkafka.taxiprocessor.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shankhadeepghoshal.learnkafka.pojos.Taxi;
import com.shankhadeepghoshal.learnkafka.pojos.TaxiEntity;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessTaxiDistanceService {
  GenericObjectPool<StatefulRedisConnection<String, String>> pool;
  TaxiCalculationService taxiCalculationService;
  ObjectMapper objectMapper;

  public String calculateTotalDistanceByTaxi(final Taxi taxi)
      throws Exception { // The Library throws Exception, not me
    log.info("Taxi data from REDIS lookup {}", taxi);
    try (final var connection = pool.borrowObject()) {
      final var commands = connection.sync();

      return taxiCalculationService.calculateDriverDistance(
          taxi,
          taxiId -> commands.get(String.valueOf(taxiId)),
          (taxiId, dataToPersist) -> commands.set(String.valueOf(taxiId), dataToPersist),
          objectMapper);
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
