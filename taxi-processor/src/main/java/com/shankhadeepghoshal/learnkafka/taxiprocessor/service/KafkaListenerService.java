package com.shankhadeepghoshal.learnkafka.taxiprocessor.service;

import com.shankhadeepghoshal.learnkafka.pojos.Taxi;
import com.shankhadeepghoshal.learnkafka.pojos.TaxiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerService {
  private static final String OK = "OK";
  ProcessTaxiDistanceService processTaxiDistanceService;
  KafkaTemplate<Long, TaxiResponse> kafkaTemplate;

  @Value("${spring.kafka.producer.topic}")
  String producerTopic;

  @KafkaListener(
      topics = "#{'${spring.kafka.consumer.topic}'}",
      groupId = "${spring.kafka.group.id}")
  public void singleMessageConsumerWithManualAck(Taxi message, Acknowledgment acknowledgment) {
    try {
      final var redisResult = processTaxiDistanceService.calculateTotalDistanceByTaxi(message);
      if (OK.equalsIgnoreCase(redisResult)) {
        final var latestTaxiData = processTaxiDistanceService.getTaxiEntityById(message.id());
        final var taxiResponse = new TaxiResponse(message.id(), latestTaxiData.totalDistance());
        kafkaTemplate
            .send(producerTopic, taxiResponse)
            .addCallback(
                new ListenableFutureCallback<>() {
                  @Override
                  public void onFailure(Throwable ex) {
                    log.error("Failed to send to output queue", ex);
                  }

                  @Override
                  public void onSuccess(SendResult<Long, TaxiResponse> result) {
                    log.info("For car {}", result.getProducerRecord().value());
                    acknowledgment.acknowledge();
                  }
                });
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
