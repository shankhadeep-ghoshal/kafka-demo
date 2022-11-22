package com.shankhadeepghoshal.learnkafka.taxiresult.service;

import com.shankhadeepghoshal.learnkafka.pojos.TaxiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FinalTaxiHandlingService {
  @KafkaListener(
      topics = "#{'${spring.kafka.consumer.topic}'}",
      groupId = "${spring.kafka.group.id}")
  public void singleMessageConsumerWithManualAck(
      final TaxiResponse message, final Acknowledgment acknowledgment) {
    log.info(consumeMessage(message));
    acknowledgment.acknowledge();
  }

  public String consumeMessage(final TaxiResponse message) {
    return "For Car number: " + message.id() + " travelled: " + message.totalDistance();
  }
}
