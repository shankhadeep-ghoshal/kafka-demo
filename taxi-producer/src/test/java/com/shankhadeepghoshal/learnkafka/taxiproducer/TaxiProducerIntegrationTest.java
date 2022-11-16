package com.shankhadeepghoshal.learnkafka.taxiproducer;

import com.shankhadeepghoshal.learnkafka.taxiproducer.pojos.Taxi;
import com.shankhadeepghoshal.learnkafka.taxiproducer.service.TaxiProducerServiceImpl;
import java.util.concurrent.TimeUnit;
import lombok.experimental.NonFinal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class TaxiProducerIntegrationTest {
  private static final long DURATION_MAGNITUDE = 500;
  private static final TimeUnit DURATION_UNIT = TimeUnit.MILLISECONDS;

  @Autowired @NonFinal private TaxiProducerServiceImpl service;

  @Autowired @NonFinal private TaxiProducerTestComponent testComponent;

  @Test
  void testSendDataToQueue() throws Exception {
    final var taxi = new Taxi(1L, 1.5, 2.6);
    service.sendTaxiToBroker(taxi);

    final var await = testComponent.getLatch().await(DURATION_MAGNITUDE, DURATION_UNIT);

    Assertions.assertTrue(await);
    Assertions.assertEquals(testComponent.getPayload(), taxi);
  }
}
