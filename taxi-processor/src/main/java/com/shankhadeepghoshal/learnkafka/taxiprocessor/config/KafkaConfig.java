package com.shankhadeepghoshal.learnkafka.taxiprocessor.config;

import com.shankhadeepghoshal.learnkafka.taxiprocessor.pojos.TaxiEntity;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {
  @Bean
  public KafkaTemplate<Long, TaxiEntity> kafkaTemplate(
      final ProducerFactory<Long, TaxiEntity> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }

  @Bean
  public ProducerFactory<Long, TaxiEntity> producerFactory(KafkaProperties kafkaProperties) {
    return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
  }
}
