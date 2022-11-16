package com.shankhadeepghoshal.learnkafka.taxiproducer;

import com.shankhadeepghoshal.learnkafka.taxiproducer.pojos.Taxi;
import java.util.concurrent.CountDownLatch;
import lombok.Getter;
import lombok.experimental.NonFinal;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Component
@Getter
class TaxiProducerTestComponent {
  CountDownLatch latch = new CountDownLatch(1);
  @NonFinal private Taxi payload;

  @KafkaListener(topics = "${kafka.topic.name}", groupId = "${kafka.producer.group-id}")
  public void receive(ConsumerRecord<Long, Taxi> consumerRecord) {
    payload = consumerRecord.value();
    latch.countDown();
  }
}
