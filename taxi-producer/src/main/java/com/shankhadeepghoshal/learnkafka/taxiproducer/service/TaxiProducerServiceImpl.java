package com.shankhadeepghoshal.learnkafka.taxiproducer.service;

import com.shankhadeepghoshal.learnkafka.pojos.Taxi;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaxiProducerServiceImpl implements TaxiProducerService {
  private static final TimeUnit DURATION_UNIT = TimeUnit.MILLISECONDS;

  KafkaTemplate<Long, Taxi> template;

  @Value("${kafka.topic.name}")
  String topicName;

  @Value("${kafka.record.timeout}")
  Long durationMagnitude;

  @Override
  public void sendTaxiToBroker(final Taxi taxi)
      throws ExecutionException, InterruptedException, TimeoutException {
    final var record = new ProducerRecord<>(topicName, taxi.id(), taxi);
    template.send(record).get(durationMagnitude, DURATION_UNIT).getProducerRecord().value();
  }
}
