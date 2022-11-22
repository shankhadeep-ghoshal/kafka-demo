package com.shankhadeepghoshal.learnkafka.taxiprocessor.config;

import com.shankhadeepghoshal.learnkafka.pojos.TaxiResponse;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class TaxiProcessorKafkaConfig {
  @Bean
  public NewTopic topic(
      @Value("${spring.kafka.producer.topic}") final String topicName,
      @Value("${kafka.topic.partition}") final Integer partitionCount,
      @Value("${kafka.topic.replication}") final Integer replicationFactor) {
    return TopicBuilder.name(topicName)
        .partitions(partitionCount)
        .replicas(replicationFactor)
        .compact()
        .build();
  }

  @Bean
  public KafkaTemplate<Long, TaxiResponse> kafkaTemplate(
      final ProducerFactory<Long, TaxiResponse> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }

  @Bean
  public ProducerFactory<Long, TaxiResponse> producerFactory(KafkaProperties kafkaProperties) {
    return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
  }
}
