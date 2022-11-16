package com.shankhadeepghoshal.learnkafka.taxiproducer.cofig;

import com.shankhadeepghoshal.learnkafka.taxiproducer.pojos.Taxi;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class TaxiProducerKafkaConfig {

  @Bean
  public NewTopic topic(
      @Value("${kafka.topic.name}") final String topicName,
      @Value("${kafka.topic.partition}") final Integer partitionCount,
      @Value("${kafka.topic.replication}") final Integer replicationFactor) {
    return TopicBuilder.name(topicName)
        .partitions(partitionCount)
        .replicas(replicationFactor)
        .build();
  }

  @Bean
  public KafkaTemplate<Long, Taxi> kafkaTemplate(
      final ProducerFactory<Long, Taxi> producerFactory,
      final ConcurrentKafkaListenerContainerFactory<Long, Taxi> listenerFactory) {
    final var template = new KafkaTemplate<>(producerFactory);
    listenerFactory.getContainerProperties().setMissingTopicsFatal(false);
    listenerFactory.setReplyTemplate(template);

    return template;
  }
}
