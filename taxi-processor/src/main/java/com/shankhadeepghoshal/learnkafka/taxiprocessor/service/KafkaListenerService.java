package com.shankhadeepghoshal.learnkafka.taxiprocessor.service;

import com.shankhadeepghoshal.learnkafka.pojos.Taxi;
import com.shankhadeepghoshal.learnkafka.taxiprocessor.pojos.TaxiEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerService {
  private static final String OK = "OK";
  RedisService redisService;
  KafkaTemplate<Long, TaxiEntity> kafkaTemplate;

  @Value("${spring.kafka.producer.topic}")
  String producerTopic;

  @KafkaListener(
      topics = "#{'${spring.kafka.consumer.topic}'}",
      groupId = "${spring.kafka.group.id}")
  public void singleMessageConsumerWithManualAck(
      Taxi message,
      Acknowledgment acknowledgment,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
    try {
      final var redisResult = redisService.handleRedisStuff(message);
      if (OK.equalsIgnoreCase(redisResult)) {
        final var latestTaxiData = redisService.getTaxiEntityById(message.id());
        kafkaTemplate
            .send(producerTopic, latestTaxiData)
            .addCallback(
                    new ListenableFutureCallback<>() {
                      @Override
                      public void onFailure(Throwable ex) {
                        log.error("Failed to send to output queue", ex);
                      }

                      @Override
                      public void onSuccess(SendResult<Long, TaxiEntity> result) {
                        log.info("Sent data to ");
                        acknowledgment.acknowledge();
                      }
                    });
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
